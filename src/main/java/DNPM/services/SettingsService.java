package DNPM.services;

import DNPM.database.SettingsRepository;

import java.util.Optional;

/**
 * Implementiert den Dienst zur Ermittlung von Systemeinstellungen
 */
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(final SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    /**
     * Übergibt ein <code>Optional</code> für die Einstellung mit angegebenen Namen
     * @param name Name der Einstellung
     * @return Optional mit Wert der Einstellung oder ein leeres Optional, wenn Einstellung nicht gefunden
     */
    public Optional<String> getSetting(String name) {
        var sid = settingsRepository.findByName(name);
        if (null == sid) {
            return Optional.empty();
        }
        return Optional.of(sid.getValue());
    }

    /**
     * Übergibt die SID als <code>Optional</code>
     * @return Optional mit Wert der SID
     */
    public Optional<String> getSID() {
        return getSetting("SID");
    }

    /**
     * Übergibt die Einstellung für <code>mehrere_mtb_in_mtbepisode</code>
     * @return Übergibt <code>true</code>, wenn <code>mehrere_mtb_in_mtbepisode</code> auf "Ja" gesetzt ist.
     */
    public boolean multipleMtbsInMtbEpisode() {
        var setting = getSetting("mehrere_mtb_in_mtbepisode");
        return setting.isPresent() && setting.get().equals("true");
    }
}
