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

package dev.dnpm.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;


/**
 * Mapper zum Ermitteln des Protokollauszugs für Formular "OS.Tumorkonferenz"
 *
 * @since 0.0.2
 */
public class OsTumorkonferenzToProtocolMapper implements ProcedureToProtocolMapper {

    /**
     * Wandelt eine Prozedur mit Formularnamen "OS.Tumorkonferenz" in ein {@link Optional} mit einer
     * Zeichenkette oder im Fehlerfall in ein leeres Optional um.
     * @param procedure Die Prozedur, für die eine Zusammenfassung ermittelt werden soll.
     * @return Das {@link Optional} mit, im Erfolgsfall, der Zusammenfassung für die Prozedur.
     */
    @Override
    public Optional<String> apply(Procedure procedure) {
        if ((!procedure.getFormName().equals("OS.Tumorkonferenz"))) {
            throw new AssertionError("Procedure is not of form type 'OS.Tumorkonferenz'");
        }

        var fragestellung = procedure.getValue("Fragestellung");
        var empfehlung = procedure.getValue("Empfehlung");

        if (
                null != fragestellung && !fragestellung.getString().isBlank()
                && null != empfehlung && !empfehlung.getString().isBlank()
        ) {
            return Optional.of(String.format("Fragestellung:%n%s%n%nEmpfehlung:%n%s", fragestellung.getString(), empfehlung.getString()));
        } else if (null != fragestellung && !fragestellung.getString().isBlank()) {
            return Optional.of(fragestellung.getString());
        } else if (null != empfehlung && !empfehlung.getString().isBlank()) {
            return Optional.of(empfehlung.getString());
        }

        return Optional.empty();
    }
}
