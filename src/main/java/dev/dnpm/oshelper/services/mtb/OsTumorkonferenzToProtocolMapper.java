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

import de.itc.onkostar.api.Procedure;

import java.util.Optional;


/**
 * Mapper zum Ermitteln des Protokollauszugs für Formular "OS.Tumorkonferenz"
 *
 * @since 0.0.2
 */
@Deprecated(forRemoval = true, since = "2.1.0")
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
