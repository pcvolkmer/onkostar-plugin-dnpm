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

package dev.dnpm.oshelper.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class MrMtbAnmeldungToProtocolMapper implements ProcedureToProtocolMapper {

    private final IOnkostarApi onkostarApi;

    public MrMtbAnmeldungToProtocolMapper(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    /**
     * Wandelt eine Prozedur mit Formularnamen "MR.MTB_Anmeldung" in ein {@link Optional} mit einer
     * Zeichenkette oder im Fehlerfall in ein leeres Optional um.
     *
     * @param procedure Die Prozedur, für die eine Zusammenfassung ermittelt werden soll.
     * @return Das {@link Optional} mit, im Erfolgsfall, der Zusammenfassung für die Prozedur.
     */
    @Override
    public Optional<String> apply(Procedure procedure) {
        if ((!procedure.getFormName().equals("MR.MTB_Anmeldung"))) {
            throw new AssertionError("Procedure is not of form type 'MR.MTB_Anmeldung'");
        }

        var resultParts = new ArrayList<String>();

        var fragestellung = procedure.getValue("Fragestellung");
        if (null != fragestellung && !fragestellung.getString().isBlank()) {
            resultParts.add(String.format("Fragestellung:%n%s", fragestellung.getString()));
        }

        var refEmpfehlung = procedure.getValue("Empfehlung");
        if (null != refEmpfehlung && refEmpfehlung.getInt() > 0) {
            var empfehlungsProzedur = onkostarApi.getProcedure(refEmpfehlung.getInt());
            var refEinzelempfehlungen = onkostarApi.getSubprocedures(empfehlungsProzedur.getId());

            if (null != refEinzelempfehlungen) {
                refEinzelempfehlungen.stream()
                        .sorted(Comparator.comparingInt(proc -> proc.getValue("Prioritaet").getInt()))
                        .forEach(proc -> {
                            if (proc.getFormName().equals("MR.MTB_Einzelempfehlung")) {
                                var empfehlung = proc.getValue("Empfehlung");
                                if (null != empfehlung && !empfehlung.getString().isBlank()) {
                                    resultParts.add(String.format("Empfehlung:%n%s", empfehlung.getString()));
                                }
                            }
                        });
            }
        }

        if (resultParts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(String.join("\n\n", resultParts));
    }
}
