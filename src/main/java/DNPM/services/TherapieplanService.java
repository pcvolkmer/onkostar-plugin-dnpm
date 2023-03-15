package DNPM.services;

import de.itc.onkostar.api.Procedure;

public interface TherapieplanService {

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in weiteren Bereichen
     * "Humangenetische Beratung" und "Reevaluation" und Unterformularen, wenn erforderlich.
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    void updateRequiredMtbEntries(Procedure procedure);

}
