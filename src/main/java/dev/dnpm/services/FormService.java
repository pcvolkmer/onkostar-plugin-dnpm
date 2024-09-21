package dev.dnpm.services;

import dev.dnpm.exceptions.FormException;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.constants.JaNeinUnbekannt;

import java.util.List;

public interface FormService {

    /**
     * Diese Methode übergibt die Prozedur-ID des zugehörigen Hauptformulars zu einem Unterformular
     * Siehe auch: <a href="https://github.com/CCC-MF/onkostar-plugin-forminfo/blob/master/src/main/java/de/ukw/ccc/onkostar/forminfo/services/FormInfoService.java">FormInfoService.java</a>
     *
     * @param procedureId Die Prozedur-ID des Unterformulars
     * @return Die Prozedur-ID des zugehörigen Hauptformulars
     * @throws FormException Wird geworfen, wenn ein Fehler auftrat
     */
    int getMainFormProcedureId(int procedureId) throws FormException;

    /**
     * Diese Methode übergibt die Prozedur-IDs von Unterformularen zu einem Formular
     * Siehe auch: <a href="https://github.com/CCC-MF/onkostar-plugin-forminfo/blob/master/src/main/java/de/ukw/ccc/onkostar/forminfo/services/FormInfoService.java">FormInfoService.java</a>
     *
     * @param procedureId Die Prozedur-ID des Formulars
     * @return Eine Liste mit Prozedur-IDs der Unterformulare
     */
    List<Integer> getSubFormProcedureIds(int procedureId);

    /**
     * Prüft, ob ein Formularfeld in der Prozedur einen Wert hat oder null ist
     * @param procedure Die zu prüfende Prozedur
     * @param fieldName Der Formularfeldname
     * @return Gibt <code>true</code> zurück, wenn das Feld einen Wert hat
     */
    static boolean hasValue(final Procedure procedure, final String fieldName) {
        return null != procedure.getValue(fieldName);
    }

    /**
     * Prüft, ob ein Formularfeld mit Ja/Nein/Unbekannt den Wert Ja hat
     * @param procedure Die zu prüfende Prozedur
     * @param fieldName Der Formularfeldname
     * @return Gibt <code>true</code> zurück, wenn das Feld den Wert "Ja" hat
     */
    static boolean isYes(final Procedure procedure, final String fieldName) {
        return hasValue(procedure, fieldName)
                && procedure.getValue(fieldName).getString().equals(JaNeinUnbekannt.JA.getCode());
    }

}
