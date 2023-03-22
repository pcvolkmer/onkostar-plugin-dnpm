package DNPM.services.systemtherapie;

import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Map;

/**
 * Service für Systemtherapieformulare
 *
 *  @since 0.2.0
 */
public interface SystemtherapieService {
    /**
     * Ermittelt eine Zusammenfassung der systemischen Therapien für eine Erkrankung
     * @param diseaseId Die ID der Erkrankung
     * @return Die Zusammenfassung der systemischen Therapien
     */
    List<Map<String, String>> getSystemischeTherapienFromDiagnose(int diseaseId);

    /**
     * Erstellt den Mapper for die Prozedur
     * @param procedure Die Prozedur für die ein Mapper erstellt werden soll
     * @return Der erstellte ProzedurToProzedurwerteMapper
     */
    ProzedurToProzedurwerteMapper prozedurToProzedurwerteMapper(Procedure procedure);

    /**
     * Ermittelt den Formularnamen anhand der SID
     * @param sid die SID
     * @return Den ermittelten Formularnamen
     */
    String selectFormNameBySID(String sid);
}
