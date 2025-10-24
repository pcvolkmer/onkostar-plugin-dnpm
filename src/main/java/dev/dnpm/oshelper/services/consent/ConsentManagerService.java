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
