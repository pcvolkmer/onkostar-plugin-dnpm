package DNPM.analyzer;

import DNPM.VerweisVon;
import DNPM.security.IllegalSecuredObjectAccessException;
import DNPM.security.PermissionType;
import DNPM.security.PersonPoolBasedPermissionEvaluator;
import DNPM.services.systemtherapie.SystemtherapieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DNPMHelper extends BackendService {

    private static final Logger logger = LoggerFactory.getLogger(DNPMHelper.class);

    private final IOnkostarApi onkostarApi;

    private final SystemtherapieService systemtherapieService;

    private final PersonPoolBasedPermissionEvaluator personPoolBasedPermissionEvaluator;

    public DNPMHelper(
            final IOnkostarApi onkostarApi,
            final SystemtherapieService systemtherapieService,
            final PersonPoolBasedPermissionEvaluator permissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.systemtherapieService = systemtherapieService;
        this.personPoolBasedPermissionEvaluator = permissionEvaluator;
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

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getVerweise(final Map<String, Object> input) {
        var procedureId = AnalyzerUtils.getRequiredId(input, "ProcedureId");
        var patientId = AnalyzerUtils.getRequiredId(input, "PatientId");

        if (procedureId.isEmpty() || patientId.isEmpty()) {
            return null;
        }

        var verbundeneFormulare = new ArrayList<Map<String, String>>();

        try {
            SessionFactory sessionFactory = onkostarApi.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();

            String sql = "SELECT prozedur.id AS procedure_id, prozedur.data_form_id, data_catalogue.name AS data_catalogue, data_catalogue_entry.name AS data_catalogue_entry, data_form.description AS formname, prozedur.beginndatum AS datum " +
                    "FROM prozedur " +
                    "LEFT JOIN data_form_data_catalogue ON data_form_data_catalogue.data_form_id = prozedur.data_form_id " +
                    "LEFT JOIN data_catalogue_entry ON data_catalogue_entry.data_catalogue_id = data_form_data_catalogue.data_catalogue_id " +
                    "LEFT JOIN data_catalogue ON data_catalogue.id = data_catalogue_entry.data_catalogue_id " +
                    "LEFT JOIN data_form ON data_form.id = prozedur.data_form_id " +
                    "WHERE patient_id = " + patientId.get() + " " +
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
                int value = 0;
                for (VerweisVon verweisVon : result) {
                    sql = verweisVon.getSQL();
                    query = session.createSQLQuery(sql)
                            .addScalar("value", StandardBasicTypes.INTEGER);
                    if (query.uniqueResult() != null) {
                        value = (Integer) query.uniqueResult();
                    }
                    if (value == procedureId.get()) {
                        verbundeneFormulare.add(Map.of("formular", verweisVon.getVerbundenesFormular()));
                        value = 0;
                    }
                }
            } catch (Exception e) {
                logger.warn("Fehler beim Hinzufügen eines Formularverweises", e);
            }
        } catch (Exception e) {
            logger.error("Fehler beim Ermitteln der Formularverweise", e);
            return null;
        }
        return verbundeneFormulare;
    }

    public List<Map<String, String>> getSystemischeTherapienFromDiagnose(final Map<String, Object> input) {
        var diagnoseId = AnalyzerUtils.getRequiredId(input, "DiagnoseId");
        if (diagnoseId.isEmpty()) {
            logger.error("Kein Parameter 'DiagnoseId' angegeben, gebe 'null' zurück");
            return null;
        }

        return systemtherapieService.getSystemischeTherapienFromDiagnose(diagnoseId.get());
    }

    public String getProzedurenFromDiagnose(final Map<String, Object> input) {
        // Prozedur, Feldname, Wert
        var dataForm = AnalyzerUtils.getRequiredValue(input, "dataForm", String.class);
        var diagnoseId = AnalyzerUtils.getRequiredId(input, "DiagnoseId");
        var patientId = AnalyzerUtils.getRequiredId(input, "PatientId");

        if (dataForm.isEmpty() || diagnoseId.isEmpty() || patientId.isEmpty()) {
            return "";
        }

        var formulare = new ArrayList<Map<String, Object>>();
        List<Procedure> prozeduren = onkostarApi.getProceduresByPatientId(patientId.get());
        for (Procedure Prozedur : prozeduren) {
            // Formular gehört zur aktuellen Diagnose und hat den angegebenen Namen
            if (Prozedur.getDiseaseIds().contains(diagnoseId.get()) && Prozedur.getFormName().contains(dataForm.get())) {
                // alle Werte auslesen
                // System.out.println(WerteListe.getKey() + ": " + WerteListe.getValue());
                formulare.add(Map.of(
                        "Formular", Prozedur.getFormName(),
                        "Felder", new HashMap<>(Prozedur.getAllValues())
                ));
            }
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(formulare);
        } catch (JsonProcessingException e) {
            logger.error("Kann Formulare nicht in JSON mappen", e);
        }
        return "";
    }

    public Object getEmpfehlung(final Map<String, Object> input) {
        var procedureID = AnalyzerUtils.getRequiredId(input, "ProcedureID");

        if (procedureID.isEmpty()) {
            logger.error("Kein Parameter 'ProcedureID' angegeben, gebe 'null' zurück");
            return null;
        }

        try {
            SessionFactory sessionFactory = onkostarApi.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            var sql = "SELECT prozedur.id, genname, geneid, geneidlink, empfehlung, beginndatum FROM prozedur "
                    + "LEFT JOIN dk_mtb_einzelempfehlung em ON em.id = prozedur.id "
                    + "JOIN data_form df ON prozedur.data_form_id = df.id AND df.name = 'MR.MTB_Einzelempfehlung' "
                    + "WHERE prozedur.hauptprozedur_id = " + procedureID.get() + " AND prozedur.geloescht = 0 "
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
        var rid = AnalyzerUtils.getRequiredId(input, "rid");
        if (rid.isEmpty()) {
            logger.error("Kein Parameter 'rid' angegeben, gebe 'false' zurück");
            return false;
        }

        var strDate = AnalyzerUtils.getRequiredValueMatching(input, "bd", "[\\d]{4}-[\\d]{2}-[\\d]{2}");
        if (strDate.isEmpty()) {
            logger.error("Kein oder ungültiger Parameter 'bd' angegeben, gebe 'false' zurück");
            return false;
        }

        //String strD = strDate.toString();
        //String CompareDate = strD.substring(1, 11);
        //DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        try {
            String sql = "UPDATE prozedur SET beginndatum = '" + strDate.get() + "' WHERE id = '" + rid.get() + "' ";
            SQLQuery result = onkostarApi.getSessionFactory().getCurrentSession().createSQLQuery(sql);
            result.executeUpdate();
            return true;
        } catch (Exception e) {
            return "Achtung: Ein Fehler ist aufgetreten, Änderung konnte nicht gespeichert werden!";
            //return null;
        }

    }

    // TODO Achtung, keine Sicherheitsprüfung, darüber kann für jeden Patienten die Liste mit ECOG-Status abgerufen werden!
    public List<SystemtherapieService.EcogStatusWithDate> getEcogStatus(final Map<String, Object> input) {
        var pid = AnalyzerUtils.getRequiredId(input, "PatientId");
        if (pid.isEmpty()) {
            logger.error("Kein Parameter 'PatientId' angegeben, gebe leere Liste zurück");
            return List.of();
        }

        var patient = onkostarApi.getPatient(pid.get());
        if (null == patient) {
            logger.error("Patient nicht gefunden, gebe leere Liste zurück");
            return List.of();
        }

        if (personPoolBasedPermissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), patient, PermissionType.READ)) {
            return systemtherapieService.ecogSatus(patient);
        }

        throw new IllegalSecuredObjectAccessException("Kein Zugriff auf diesen Patienten");
    }
}