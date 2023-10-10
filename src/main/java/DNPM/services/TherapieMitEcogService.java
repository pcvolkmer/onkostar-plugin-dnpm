package DNPM.services;

import DNPM.dto.EcogStatusWithDate;
import de.itc.onkostar.api.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstelle zum Ermitteln von ECOG-Statusinformationen
 *
 *  @since 0.6.0
 */
public interface TherapieMitEcogService {

    /**
     * Ermittelt den letzten bekannten ECOG-Status aus allen Therapieformularen des Patienten
     * @param patient Der zu verwendende Patient
     * @return Der ECOG-Status als String oder leeres Optional
     */
    Optional<String> latestEcogStatus(Patient patient);

    /**
     * Ermittelt jeden bekannten ECOG-Status aus allen Therapieformularen des Patienten
     * @param patient Der zu verwendende Patient
     * @return Eine Liste mit Datum und ECOG-Status als String
     */
    List<EcogStatusWithDate> ecogStatus(Patient patient);

}
