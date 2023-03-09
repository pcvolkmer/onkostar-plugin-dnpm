package DNPM;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;

public class ConsentManager implements IProcedureAnalyzer {

  @Autowired
  private IOnkostarApi onkostarApi;

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
	public boolean isRelevantForAnalyzer(Procedure Prozedur, Disease Erkrankung) {
		return Prozedur.getFormName().equals(onkostarApi.getGlobalSetting("consentform"));
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
  public void analyze(Procedure Prozedur, Disease Erkrankung) {
	  int value = 0;
	  try {
      SessionFactory sessionFactory = onkostarApi.getSessionFactory();
      Session session = sessionFactory.getCurrentSession();
   // geänderte Werte checken
      String sql1 = "select id, max(timestamp) AS datum from aenderungsprotokoll where entity_id = '" + Prozedur.getId() + "'";
      SQLQuery query1 = session.createSQLQuery(sql1)
          .addScalar("id", StandardBasicTypes.INTEGER)
          .addScalar("datum", StandardBasicTypes.TIMESTAMP);
      System.out.println(query1.uniqueResult().toString());

      try {
        String sql = "SELECT prozedur.id AS procedure_id, prozedur.data_form_id, data_catalogue.name AS data_catalogue, data_catalogue_entry.name AS data_catalogue_entry, data_form.description AS formname, prozedur.beginndatum AS datum " + 
            "FROM prozedur " +
            "LEFT JOIN data_form_data_catalogue ON data_form_data_catalogue.data_form_id = prozedur.data_form_id " +
            "LEFT JOIN data_catalogue_entry ON data_catalogue_entry.data_catalogue_id = data_form_data_catalogue.data_catalogue_id " +
            "LEFT JOIN data_catalogue ON data_catalogue.id = data_catalogue_entry.data_catalogue_id " +
            "LEFT JOIN data_form ON data_form.id = prozedur.data_form_id " +
            "WHERE patient_id = " + Prozedur.getPatientId() + " " +
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
              value = (Integer)query.uniqueResult();
            }
            if (value == Prozedur.getId()) {
            	Procedure andereprozedur = onkostarApi.getProcedure(var.getProcedure_id());
            	try {
            	  Map<String, Item> Felder = Prozedur.getAllValues();
            	  for (Map.Entry<String, Item> Feld: Felder.entrySet()) {
            	    if (Feld.getKey().length() > 6 && Feld.getKey().substring(0, 7).equals("Consent")) {
            	      if (Feld.getKey().equals("ConsentStatusEinwilligungDNPM")) {
            	        switch (Feld.getValue().getValue().toString()) {
                      case "z":
                        andereprozedur.setValue(Feld.getKey(), new Item(Feld.getKey(), "active"));
                        break;
                      case "a":
                        andereprozedur.setValue(Feld.getKey(), new Item(Feld.getKey(), "rejected"));
                        break;
                      case "w":
                        andereprozedur.setValue(Feld.getKey(), new Item(Feld.getKey(), "rejected"));
                        break;
            	        }
            	      } else {
            	        andereprozedur.setValue(Feld.getKey(), Prozedur.getValue(Feld.getKey()));
            	      }
            	    }
            	  }
            		onkostarApi.saveProcedure(andereprozedur);
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
              value = 0;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (Exception e) {
      	System.out.println(e.getMessage());
      }
	  } catch (Exception e) {
    	System.out.println(e.getMessage());
    }
	}
	
}
