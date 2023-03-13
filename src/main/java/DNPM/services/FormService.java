package DNPM.services;

import DNPM.exceptions.FormException;

import java.util.List;

public interface FormService {

    /**
     * Diese Methode übergibt die Prozedur-ID des zugehörigen Hauptformulars zu einem Unterformular
     * Siehe auch: <a href="https://github.com/CCC-MF/onkostar-plugin-forminfo/blob/master/src/main/java/de/ukw/ccc/onkostar/forminfo/services/FormInfoService.java">FormInfoService.java</a>
     *
     * @param procedureId Die Prozedur-ID des Unterformulars
     * @return Die Prozedur-ID des zugehörigen Hauptformulars
     * @throws FormException
     */
    int getMainFormProcedureId(int procedureId) throws FormException;

    /**
     * Diese Methode übergibt die Prozedur-IDs von Unterformularen zu einem Formular
     * Siehe auch: <a href="https://github.com/CCC-MF/onkostar-plugin-forminfo/blob/master/src/main/java/de/ukw/ccc/onkostar/forminfo/services/FormInfoService.java">FormInfoService.java</a>
     *
     * @param procedureId Die Prozedur-ID des Formulars
     * @return Eine Liste mit Prozedur-IDs der Unterformulare
     * @throws FormException
     */
    List<Integer> getSubFormProcedureIds(int procedureId);

}
