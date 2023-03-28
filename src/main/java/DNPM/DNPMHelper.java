package DNPM;

import DNPM.services.systemtherapie.SystemtherapieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DNPMHelper implements IProcedureAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(DNPMHelper.class);

    private final IOnkostarApi onkostarApi;

    private final SystemtherapieService systemtherapieService;

    public DNPMHelper(final IOnkostarApi onkostarApi, final SystemtherapieService systemtherapieService) {
        this.onkostarApi = onkostarApi;
        this.systemtherapieService = systemtherapieService;
    }

    @Override
    public OnkostarPluginType getType() {
        // Typ des Plugins
        // Für das Interface IProcedureAnalyzer gültig sind ANALYZER und BACKEND_SERVICE
        return OnkostarPluginType.BACKEND_SERVICE;
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public String getName() {
        return "UMR DNPM";
    }

    @Override
    public String getDescription() {
        return "Methoden für DNPM-Formulare";
    }

    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }

    @Override
    public boolean isSynchronous() {
        return true;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure entry, Disease currentDisease) {
        // Plugin enthält nur Methoden für Formulare und soll nicht ausgeführt werden
        return false;
    }

    @Override
    public void analyze(Procedure entry, Disease currentDisease) {
        // wird nicht benötigt, da dass Plugin nicht ausgeführt wird
    }

    @SuppressWarnings("unchecked")
    public Object getVerweise(final Map<String, Object> input) {
        int ProcedureId = (int) input.get("ProcedureId");
        int PatientId = (int) input.get("PatientId");
        int value = 0;
        List<Map<String, String>> VerbundeneFormulare = new ArrayList<Map<String, String>>();

        try {
            SessionFactory sessionFactory = onkostarApi.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            try {
                String sql = "SELECT prozedur.id AS procedure_id, prozedur.data_form_id, data_catalogue.name AS data_catalogue, data_catalogue_entry.name AS data_catalogue_entry, data_form.description AS formname, prozedur.beginndatum AS datum " +
                        "FROM prozedur " +
                        "LEFT JOIN data_form_data_catalogue ON data_form_data_catalogue.data_form_id = prozedur.data_form_id " +
                        "LEFT JOIN data_catalogue_entry ON data_catalogue_entry.data_catalogue_id = data_form_data_catalogue.data_catalogue_id " +
                        "LEFT JOIN data_catalogue ON data_catalogue.id = data_catalogue_entry.data_catalogue_id " +
                        "LEFT JOIN data_form ON data_form.id = prozedur.data_form_id " +
                        "WHERE patient_id = " + PatientId + " " +
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
                List<VerweisVon> result = query.list();
                try {
                    for (VerweisVon var : result) {
                        sql = var.getSQL();
                        query = session.createSQLQuery(sql)
                                .addScalar("value", StandardBasicTypes.INTEGER);
                        if (query.uniqueResult() != null) {
                            value = (Integer) query.uniqueResult();
                        }
                        if (value == ProcedureId) {
                            VerbundeneFormulare.add(Map.of("formular", var.getVerbundenesFormular()));
                            value = 0;
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return VerbundeneFormulare;
    }

    public List<Map<String, String>> getSystemischeTherapienFromDiagnose(final Map<String, Object> input) {
        var diagnoseId = input.get("DiagnoseId");

        if (null == diagnoseId || Integer.parseInt(diagnoseId.toString()) == 0) {
            logger.error("Kein Parameter 'DiagnoseId' angegeben, gebe 'null' zurück");
            return null;
        }

        return systemtherapieService.getSystemischeTherapienFromDiagnose(Integer.parseInt(diagnoseId.toString()));
    }

    public Object getProzedurenFromDiagnose(final Map<String, Object> input) {
        String dataForm = (String) input.get("dataForm");
        int DiagnoseId = (int) input.get("DiagnoseId");
        int PatientId = (int) input.get("PatientId");
        // Prozedur, Feldname, Wert

        List<Object> Formulare = new ArrayList<Object>();
        String jsonStr = "";
        List<Procedure> Prozeduren = onkostarApi.getProceduresByPatientId(PatientId);
        for (Procedure Prozedur : Prozeduren) {
            // Formular gehört zur aktuellen Diagnose und hat den angegebenen Namen
            if (Prozedur.getDiseaseIds().contains(DiagnoseId) && Prozedur.getFormName().contains(dataForm)) {
                // alle Werte auslesen
                Map<String, Item> Werte = Prozedur.getAllValues();
                Map<String, Object> Values = new HashMap<>();
                for (Map.Entry<String, Item> WerteListe : Werte.entrySet()) {
                    Values.put(WerteListe.getKey(), WerteListe.getValue());
//          System.out.println(WerteListe.getKey() + ": " + WerteListe.getValue());
                }
                Map<String, Object> Formular = new HashMap<>();
                Formular.put("Formular", Prozedur.getFormName());
                Formular.put("Felder", Values);
                Formulare.add(Formular);
            }
        }
        ObjectMapper Obj = new ObjectMapper();
        try {
            jsonStr = Obj.writeValueAsString(Formulare);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonStr;
    }

    public Object getEmpfehlung(final Map<String, Object> input) {
        var procedureID = input.get("ProcedureID");

        if (null == procedureID || Integer.parseInt(procedureID.toString()) == 0) {
            logger.error("Kein Parameter 'ProcedureID' angegeben, gebe 'null' zurück");
            return null;
        }

        try {
            SessionFactory sessionFactory = onkostarApi.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            var sql = "SELECT prozedur.id, genname, geneid, geneidlink, empfehlung, beginndatum FROM prozedur "
                    + "LEFT JOIN dk_mtb_einzelempfehlung em ON em.id = prozedur.id "
                    + "JOIN data_form df ON prozedur.data_form_id = df.id AND df.name = 'MR.MTB_Einzelempfehlung' "
                    + "WHERE prozedur.hauptprozedur_id = " + Integer.parseInt(procedureID.toString()) + " AND prozedur.geloescht = 0 "
                    + "ORDER BY beginndatum";

            SQLQuery query = session.createSQLQuery(sql)
                    .addScalar("id", StandardBasicTypes.STRING)
                    .addScalar("genname", StandardBasicTypes.STRING)
                    .addScalar("geneid", StandardBasicTypes.STRING)
                    .addScalar("geneidlink", StandardBasicTypes.STRING)
                    .addScalar("empfehlung", StandardBasicTypes.STRING)
                    .addScalar("beginndatum", StandardBasicTypes.STRING);

            @SuppressWarnings("unchecked")
            List<String[]> rows = query.list();
            return rows;
        } catch (Exception e) {
            logger.error("Fehler bei Abfrage von Empfehlungen", e);
            return null;
        }
    }

    public Object updateEmpfehlungPrio(final Map<String, Object> input) {
        // Auslesen und Prüfen der Parameter aus 'input'
        var rid = input.get("rid");
        if (null == rid || Integer.parseInt(rid.toString()) == 0) {
            logger.error("Kein Parameter 'rid' angegeben, gebe 'false' zurück");
            return false;
        }

        var strDate = input.get("bd");
        if (null == strDate || !strDate.toString().matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
            logger.error("Kein oder ungültiger Parameter 'bd' angegeben, gebe 'false' zurück");
            return false;
        }

        //String strD = strDate.toString();
        //String CompareDate = strD.substring(1, 11);
        //DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        try {
            String sql = "UPDATE prozedur SET beginndatum = '" + strDate + "' WHERE id = '" + rid + "' ";
            SQLQuery result = onkostarApi.getSessionFactory().getCurrentSession().createSQLQuery(sql);
            result.executeUpdate();
            return true;
        } catch (Exception e) {
            return "Achtung: Ein Fehler ist aufgetreten, Änderung konnte nicht gespeichert werden!";
            //return null;
        }

    }
}