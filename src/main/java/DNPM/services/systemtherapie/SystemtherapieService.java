package DNPM.services.systemtherapie;

import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Ermittelt den letzten bekannten ECOG-Status aus allen Systemtherapieformularen des Patienten
     * @param patient Der zu verwendende Patient
     * @return Der ECOG-Status als String oder leeres Optional
     */
    Optional<String> latestEcogStatus(Patient patient);

    /**
     * Ermittelt jeden bekannten ECOG-Status aus allen Systemtherapieformularen des Patienten
     * @param patient Der zu verwendende Patient
     * @return Eine Liste mit Datum und ECOG-Status als String
     */
    List<EcogStatusWithDate> ecogStatus(Patient patient);

    /**
     * Datenklasse zum Abbilden des ECOG-Status und Datum
     */
    class EcogStatusWithDate {
        private Date date;
        private String status;

        public EcogStatusWithDate(Date date, String status) {
            Assert.notNull(date, "Date cannot be null");
            Assert.hasText(status, "Status cannot be empty String");
            Assert.isTrue(isValidEcogCode(status), "Not a valid ADT.LeistungszustandECOG code");
            this.date = date;
            this.status = status;
        }

        private boolean isValidEcogCode(String status) {
            switch (status) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "U":
                    return true;
                default:
                    return false;
            }
        }

        public Date getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }
    }

}
