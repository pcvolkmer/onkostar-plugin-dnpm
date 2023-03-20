package DNPM.services;

import de.itc.onkostar.api.Procedure;

import java.util.List;

public interface TherapieplanService {

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in weiteren Bereichen
     * "Humangenetische Beratung" und "Reevaluation" und Unterformularen, wenn erforderlich.
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    void updateRequiredMtbEntries(Procedure procedure);

    /**
     * Finde verlinkte MTBs in Hauptformular und Unterformularen
     *
     * @param procedure Die Prozedur mit Hauptformular
     * @return Liste mit verlinkten MTBs
     */
    List<Procedure> findReferencedMtbs(Procedure procedure);

    /**
     * Finde verlinkte MTBs in Hauptformular und Unterformularen
     *
     * @param procedureId ID der Prozedur mit Hauptformular
     * @return Liste mit verlinkten MTBs
     */
    List<Procedure> findReferencedMtbs(int procedureId);

}
