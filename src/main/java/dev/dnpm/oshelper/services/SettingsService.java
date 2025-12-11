/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
