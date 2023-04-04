package DNPM.services.consent;

import de.itc.onkostar.api.Procedure;

/**
 * Schnittstelle für die Anwendung von Consent-Änderungen
 *
 * @since 0.2.0
 */
public interface ConsentManagerService {

    /**
     * Wende Consent an, wenn dieses Consent-Formular gespeichert wird
     * @param procedure Prozedur des Consent-Formulars
     */
    void applyConsent(Procedure procedure);

    /**
     * Optionale Prüfung, ob die angegebene Prozedur angewendet werden kann.
     * @param procedure Anzuwendende Prozedur
     * @return Gibt <code>true</code> zurück, wenn die Prozedur angewendet werden kann.
     */
    default boolean canApply(Procedure procedure) {
        return null != procedure;
    }

}
