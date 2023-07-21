package DNPM;

import DNPM.analyzer.AnalyzerUtils;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Merkmalskatalog implements IProcedureAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    public Merkmalskatalog(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.BACKEND_SERVICE;
    }

    @Override
    public String getVersion() {
        return "0.3.1";
    }

    @Override
    public String getName() {
        return "UMR Merkmalskatalog";
    }

    @Override
    public String getDescription() {
        return "Methoden für Merkmalskataloge";
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
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease currentDisease) {
        return false;
    }

    @Override
    public void analyze(Procedure procedure, Disease disease) {
    }

    public List<String[]> getMerkmalskatalog(final Map<String, Object> input) {
        var merkmalskatalog = AnalyzerUtils.getRequiredValue(input, "Merkmalskatalog", String.class);
        var spalten = AnalyzerUtils.getRequiredValue(input, "Spalten", String.class);

        if (merkmalskatalog.isEmpty()) {
            logger.error("Kein Merkmalskatalog angegeben!");
            return null;
        }

        if (spalten.isEmpty()) {
            logger.error("Keine Spalten angegeben!");
            return null;
        }

        String[] spaltenArray = spalten.get().split("\\s*,\\s*");

        try {
            SessionFactory sessionFactory = onkostarApi.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();

            String sql = "SELECT p.id, p.code, p.shortdesc, p.description, p.note, p.synonyms "
                    + "FROM property_catalogue "
                    + "LEFT JOIN property_catalogue_version ON property_catalogue_version.datacatalog_id = property_catalogue.id "
                    + "LEFT JOIN property_catalogue_version_entry p ON p.property_version_id = property_catalogue_version.id "
                    + "WHERE name = '" + merkmalskatalog.get() + "' AND aktiv = 1 "
                    + "ORDER BY position ASC";

            SQLQuery query = session.createSQLQuery(sql);

            for (String s : spaltenArray) {
                query.addScalar(s, StandardBasicTypes.STRING);
            }

            @SuppressWarnings("unchecked")
            List<String[]> rows = query.list();
            return rows;
        } catch (Exception e) {
            logger.error("Fehler bei der Ausführung von getMerkmalskatalog()", e);
            return null;
        }
    }
}
