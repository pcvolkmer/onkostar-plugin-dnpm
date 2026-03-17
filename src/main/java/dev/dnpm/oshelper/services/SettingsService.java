/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (C) 2023-2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.oshelper.services;

import dev.dnpm.oshelper.database.SettingsRepository;

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
    @Deprecated(since = "2.1.0", forRemoval = true)
    public boolean multipleMtbsInMtbEpisode() {
        var setting = getSetting("mehrere_mtb_in_mtbepisode");
        return setting.isPresent() && setting.get().equals("true");
    }
}
