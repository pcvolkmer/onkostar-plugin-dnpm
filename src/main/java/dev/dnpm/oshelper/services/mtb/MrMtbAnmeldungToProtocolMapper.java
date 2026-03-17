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

package dev.dnpm.oshelper.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

@Deprecated(forRemoval = true, since = "2.1.0")
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
