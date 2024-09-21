package dev.dnpm.services.systemtherapie;

import dev.dnpm.services.TherapieMitEcogService;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Map;

/**
 * Service für Systemtherapieformulare
 *
 *  @since 0.2.0
 */
public interface SystemtherapieService extends TherapieMitEcogService {
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

}
