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

package dev.dnpm.services.systemtherapie;

import dev.dnpm.services.TherapieMitEcogService;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Map;

/**
 * Service für Systemtherapieformulare
 *
 *  @since 0.2.0
 */
public interface SystemtherapieService extends TherapieMitEcogService {
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

}
