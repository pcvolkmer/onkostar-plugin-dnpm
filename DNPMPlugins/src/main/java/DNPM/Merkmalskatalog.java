package DNPM;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;

public class Merkmalskatalog implements IProcedureAnalyzer{
  @Autowired
  private IOnkostarApi onkostarApi;

  @Override
  public OnkostarPluginType getType() {
    return OnkostarPluginType.BACKEND_SERVICE;
  }

  @Override
  public String getVersion() {
    return "1";
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
  public void analyze(Procedure procedurea, Disease disease) {}

  public Object getMerkmalskatalog(final Map<String, Object> input) {
    String Merkmalskatalog = (String) input.get("Merkmalskatalog");
    String Spalten = (String) input.get("Spalten");
    String[] SpaltenArray = Spalten.split("\\s*,\\s*");
    String sql;
    try {
      SessionFactory sessionFactory = onkostarApi.getSessionFactory();
      Session session = sessionFactory.getCurrentSession();	
      try {
        sql = "SELECT p.id, p.code, p.shortdesc, p.description, p.note, p.synonyms " 
                  + "FROM property_catalogue "
                  + "LEFT JOIN property_catalogue_version ON property_catalogue_version.datacatalog_id = property_catalogue.id "
                  + "LEFT JOIN property_catalogue_version_entry p ON p.property_version_id = property_catalogue_version.id "
                  + "WHERE name = '" + Merkmalskatalog + "' AND aktiv = 1 "
                  + "ORDER BY position ASC";
	    	
        SQLQuery query = session.createSQLQuery(sql);

        for (int i = 0; i < SpaltenArray.length; i++) {
          query.addScalar(SpaltenArray[i], StandardBasicTypes.STRING);
        }

        @SuppressWarnings("unchecked")
        List<String[]> rows = query.list();
        return rows;
      } catch (Exception e) {
        return null;
      }
    } catch (Exception e) {
      return null;
    }	    		
  }
}
