package dev.dnpm.database;

import de.itc.db.dnpm.Setting;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository zum Lesen von Einstellungen
 */
@Repository("dnpmSettingRepository")
public interface SettingsRepository extends ReadOnlyRepository<Setting, Long> {

    Setting findByName(String name);

}
