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

}
