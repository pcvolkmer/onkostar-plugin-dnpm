package DNPM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ATCCodes.AtcCode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;

import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;

public class DNPMHelper implements IProcedureAnalyzer{

  // Laden der API
  @Autowired
  private IOnkostarApi onkostarApi;

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
              value = (Integer)query.uniqueResult();
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

  public Object getSystemischeTherapienFromDiagnose(final Map<String, Object> input) {
    int DiagnoseId = (int) input.get("DiagnoseId");

    String jsonStr = "";
    List<Object> Rueckgabewerte = new ArrayList<Object>();
    List<Procedure> Prozeduren = onkostarApi.getProceduresForDiseaseByForm(DiagnoseId, "OS.Systemische Therapie");
    // für jede Prozedur
    for (Procedure Prozedur : Prozeduren) {
      String Beginn = new String();
      String Ende = new String();
      String Wirkstoffe = new String();
      String Beendigung = new String();
      String Ergebnis = new String();

      // SubstanzenCodesListe enthält die Liste der SubstanzenCodes
      List<Map<String, String>> SubstanzenCodesListe = new ArrayList<Map<String, String>>();
      
      // alle Werte der Prozedur auslesen
      Map<String, Item> alleWerte = Prozedur.getAllValues();
      // Prozedurwerte enthält nur die interessanten Werte
      Map<String, Object> Prozedurwerte = new HashMap<>();
      // alle Werte durchgehen und die interessanten übernehmen
      for (Map.Entry<String, Item> WerteListe : alleWerte.entrySet()) {
        // Datum des Hauptformulars merken
        if (WerteListe.getKey().equals("Beendigung")) {
          Beendigung = WerteListe.getValue().getValue();
        }
        if (WerteListe.getKey().equals("Ergebnis")) {
          Ergebnis = WerteListe.getValue().getValue();
        }
        if (WerteListe.getKey().equals("Beginn")) {
          Beginn = WerteListe.getValue().getString();// + "," + WerteListe.getValue().getDateAccuracy();
        }
        if (WerteListe.getKey().equals("Ende")) {
          Ende = WerteListe.getValue().getString();// + "," + WerteListe.getValue().getDateAccuracy();
        }
        // im Subformular (SubstanzenList) Substanzen auslesen
        if (WerteListe.getKey().equals("SubstanzenList")) {
          int Index = -1;          
          // SubstanzenCodesListe enthält die Liste der SubstanzenCodes eines Subformulars
          ArrayList<Map<String, Map<String, String>>> Subformular = new ArrayList<>();
          Subformular = WerteListe.getValue().getValue();
          // Werte aus Subformular verarbeiten
          for (Map<String, Map<String, String>> SubformularWerte: Subformular) {

            // SubstanzenCodes enthält den Code und den Namen einer Substanz
            Map<String, String> SubstanzenCodes = new HashMap<String, String>();
            // Index des Codes (Substanz)
            Index = Arrays.asList((SubformularWerte.keySet().toArray())).indexOf("Substanz");
            if (AtcCode.isAtcCode(SubformularWerte.values().toArray()[Index].toString())) {
              SubstanzenCodes.put("system", "ATC");
            } else {
              SubstanzenCodes.put("system", "other");
            }
            
            SubstanzenCodes.put("code", (String) SubformularWerte.values().toArray()[Index]);
            // Index der Substanz (Substanz_shortDescription)
            Index = Arrays.asList((SubformularWerte.keySet().toArray())).indexOf("Substanz_shortDescription");
            SubstanzenCodes.put("substance", (String) SubformularWerte.values().toArray()[Index]);
            SubstanzenCodesListe.add(SubstanzenCodes);
            Wirkstoffe = Wirkstoffe + (String) SubformularWerte.values().toArray()[Index] + ", ";
          }
        }
      }
      ObjectMapper Obj = new ObjectMapper();
      try {
        jsonStr = Obj.writeValueAsString(SubstanzenCodesListe);
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Prozedurwerte.put("Beginn", Beginn);
      Prozedurwerte.put("Ende", Ende);
      Prozedurwerte.put("Beendigung", Beendigung);
      Prozedurwerte.put("Ergebnis", Ergebnis);
      Prozedurwerte.put("Wirkstoffe", Wirkstoffe.substring(0, Wirkstoffe.length()-2));
      Prozedurwerte.put("WirkstoffCodes", jsonStr);
      Rueckgabewerte.add(Prozedurwerte);
    }
    return Rueckgabewerte;
  }

  public Object getProzedurenFromDiagnose(final Map<String, Object> input) {
    String dataForm = (String) input.get("dataForm");
    int DiagnoseId = (int) input.get("DiagnoseId");
    int PatientId = (int) input.get("PatientId");
    // Prozedur, Feldname, Wert
    
    List<Object> Formulare = new ArrayList<Object>();
    String jsonStr = "";
    List<Procedure> Prozeduren = onkostarApi.getProceduresByPatientId(PatientId);
    for (Procedure Prozedur: Prozeduren ) {
      // Formular gehört zur aktuellen Diagnose und hat den angegebenen Namen
      if (Prozedur.getDiseaseIds().contains(DiagnoseId) && Prozedur.getFormName().contains(dataForm)) {
        // alle Werte auslesen
        Map<String, Item> Werte = Prozedur.getAllValues();
        Map<String, Object> Values = new HashMap<>();
        for (Map.Entry<String, Item> WerteListe: Werte.entrySet()) {
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
	    // Auslesen der Parameter aus 'input'
	  	int ProcedureID = (int) input.get("ProcedureID");

	    String sql;
	    try {
	    	SessionFactory sessionFactory = onkostarApi.getSessionFactory();
		    Session session = sessionFactory.getCurrentSession();	
		    try {
		    	sql = "SELECT * FROM prozedur " 
		    			+ "LEFT JOIN dk_mtb_einzelempfehlung em ON em.id = prozedur.id "
		    			+ "WHERE prozedur.hauptprozedur_id = " + ProcedureID + " AND prozedur.geloescht = 0 AND prozedur.data_form_id = 489 "
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
				return null;
			}
		} catch (Exception e) {
			return null;
		}	    		
	}

  public Object updateEmpfehlungPrio(final Map<String, Object> input) {
	    // Auslesen der Parameter aus 'input'
	  	//int rid = (int) input.get("rid");
	  	Object rid = input.get("rid");
	  	Object strDate = input.get("bd");
	  	SQLQuery result = null;
	  	
	    //String strD = strDate.toString();
	    //String CompareDate = strD.substring(1, 11);
	  	
		//DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

	    String sql;

		try {
			sql = "UPDATE prozedur SET beginndatum = '"+ strDate +"' WHERE id = '"+ rid +"' ";
			result = onkostarApi.getSessionFactory().getCurrentSession().createSQLQuery(sql);
			result.executeUpdate();
			return true;
		
		} catch (Exception e) {
			return "Achtung: Ein Fehler ist aufgetreten, Änderung konnte nicht gespeichert werden!";
			//return null;
		}
	    		
	}  
}