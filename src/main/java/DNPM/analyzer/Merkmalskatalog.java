package DNPM.analyzer;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Merkmalskatalog extends BackendService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    public Merkmalskatalog(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
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
            SQLQuery query = getSqlQuery(merkmalskatalog.get());

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

    private SQLQuery getSqlQuery(String merkmalskatalog) {
        SessionFactory sessionFactory = onkostarApi.getSessionFactory();
        Session session = sessionFactory.getCurrentSession();

        String sql = "SELECT p.id, p.code, p.shortdesc, p.description, p.note, p.synonyms "
                + "FROM property_catalogue "
                + "LEFT JOIN property_catalogue_version ON property_catalogue_version.datacatalog_id = property_catalogue.id "
                + "LEFT JOIN property_catalogue_version_entry p ON p.property_version_id = property_catalogue_version.id "
                + "WHERE name = '" + merkmalskatalog + "' AND aktiv = 1 "
                + "ORDER BY position ASC";

        return session.createSQLQuery(sql);
    }
}
