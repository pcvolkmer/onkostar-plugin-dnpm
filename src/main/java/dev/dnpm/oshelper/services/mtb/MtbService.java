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

import java.util.List;
import java.util.Optional;

@Deprecated(forRemoval = true, since = "2.1.0")
public interface MtbService {
    /**
     * Zusammenfassung der Prozeduren
     * @param procedures Prozeduren, die zusammen gefasst werden sollen
     * @return Text mit Zusammenfassung der Prozeduren
     */
    String getProtocol(List<Procedure> procedures);

    /**
     * Übergibt anzuwendenden Mapper für eine Prozedur
     * @param procedure Prozedur, für die ein Mapper ermittelt werden soll
     * @return Mapper für diese Prozedur
     */
    ProcedureToProtocolMapper procedureToProtocolMapper(Procedure procedure);

    /**
     * Select mapper using method {@link #procedureToProtocolMapper(Procedure)} and apply procedure
     * @param procedure The Procedure to select mapper for and apply
     * @return {@link Optional} with protocol or empty {@link Optional}
     */
    default Optional<String> selectAndApplyMapper(Procedure procedure) {
        return this.procedureToProtocolMapper(procedure).apply(procedure);
    }
}
