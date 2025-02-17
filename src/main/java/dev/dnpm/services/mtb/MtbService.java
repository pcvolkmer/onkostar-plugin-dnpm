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

import java.util.List;
import java.util.Optional;

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
