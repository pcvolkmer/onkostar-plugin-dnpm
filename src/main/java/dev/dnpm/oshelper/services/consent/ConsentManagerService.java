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

package dev.dnpm.oshelper.services.consent;

import de.itc.onkostar.api.Procedure;

/**
 * Schnittstelle für die Anwendung von Consent-Änderungen
 *
 * @since 0.2.0
 */
@Deprecated(forRemoval = true, since = "2.0.0")
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
