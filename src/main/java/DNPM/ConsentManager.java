package DNPM;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ConsentManager implements IProcedureAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    public ConsentManager(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    @Override
    public String getDescription() {
        return "Aktualisiert Consent Daten in verknüpften Formularen";
    }

    @Override
    public String getName() {
        return "Consent Manager";
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure prozedur, Disease erkrankung) {
        return prozedur.getFormName().equals(onkostarApi.getGlobalSetting("consentform"));
    }

    @Override
    public boolean isRelevantForDeletedProcedure() {
        // TODO is relevant for deleted procedure = true
        return false;
    }

    @Override
    public boolean isSynchronous() {
        return true;
    }

    @Override
    public void analyze(Procedure prozedur, Disease erkrankung) {
        int value = 0;
        try {
            SessionFactory sessionFactory = onkostarApi.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            // geänderte Werte checken
            String sql1 = "select id, max(timestamp) AS datum from aenderungsprotokoll where entity_id = '" + prozedur.getId() + "'";
            SQLQuery query1 = session.createSQLQuery(sql1)
                    .addScalar("id", StandardBasicTypes.INTEGER)
                    .addScalar("datum", StandardBasicTypes.TIMESTAMP);
            logger.info("Wert-Check: {}", query1.uniqueResult());

            String sql = "SELECT prozedur.id AS procedure_id, prozedur.data_form_id, data_catalogue.name AS data_catalogue, data_catalogue_entry.name AS data_catalogue_entry, data_form.description AS formname, prozedur.beginndatum AS datum " +
                    "FROM prozedur " +
                    "LEFT JOIN data_form_data_catalogue ON data_form_data_catalogue.data_form_id = prozedur.data_form_id " +
                    "LEFT JOIN data_catalogue_entry ON data_catalogue_entry.data_catalogue_id = data_form_data_catalogue.data_catalogue_id " +
                    "LEFT JOIN data_catalogue ON data_catalogue.id = data_catalogue_entry.data_catalogue_id " +
                    "LEFT JOIN data_form ON data_form.id = prozedur.data_form_id " +
                    "WHERE patient_id = " + prozedur.getPatientId() + " " +
                    "AND geloescht = 0 " +
                    "AND data_catalogue_entry.type = 'formReference' " +
                    "GROUP BY prozedur.id, prozedur.data_form_id, data_catalogue.name, data_catalogue_entry.name";

            SQLQuery query = session.createSQLQuery(sql)
                    .addScalar("procedure_id", StandardBasicTypes.INTEGER)
                    .addScalar("data_form_id", StandardBasicTypes.INTEGER)
                    .addScalar("data_catalogue", StandardBasicTypes.STRING)
                    .addScalar("data_catalogue_entry", StandardBasicTypes.STRING)
                    .addScalar("formname", StandardBasicTypes.STRING)
                    .addScalar("datum", StandardBasicTypes.DATE);

            query.setResultTransformer(Transformers.aliasToBean(VerweisVon.class));

            @SuppressWarnings("unchecked")
            List<VerweisVon> result = query.list();

            for (VerweisVon verweisVon : result) {
                sql = verweisVon.getSQL();
                query = session.createSQLQuery(sql)
                        .addScalar("value", StandardBasicTypes.INTEGER);
                if (query.uniqueResult() != null) {
                    value = (Integer) query.uniqueResult();
                }
                if (value == prozedur.getId()) {
                    saveReferencedProcedure(prozedur, verweisVon);
                    value = 0;
                }
            }
        } catch (RuntimeException e) {
            logger.error("Sonstiger Fehler bei der Ausführung von analyze()", e);
        }
    }

    private void saveReferencedProcedure(Procedure prozedur, VerweisVon verweisVon) {
        Procedure andereprozedur = onkostarApi.getProcedure(verweisVon.getProcedure_id());
        try {
            Map<String, Item> felder = prozedur.getAllValues();
            for (Map.Entry<String, Item> feld : felder.entrySet()) {
                if (feld.getKey().startsWith("Consent")) {
                    if (feld.getKey().equals("ConsentStatusEinwilligungDNPM")) {
                        switch (feld.getValue().getValue().toString()) {
                            case "z":
                                andereprozedur.setValue(feld.getKey(), new Item(feld.getKey(), "active"));
                                break;
                            case "a":
                            case "w":
                                andereprozedur.setValue(feld.getKey(), new Item(feld.getKey(), "rejected"));
                                break;
                            default:
                                break;
                        }
                    } else {
                        andereprozedur.setValue(feld.getKey(), prozedur.getValue(feld.getKey()));
                    }
                }
            }
            onkostarApi.saveProcedure(andereprozedur);
        } catch (Exception e) {
            logger.error("Kann Prozedur nicht speichern", e);
        }
    }

}
