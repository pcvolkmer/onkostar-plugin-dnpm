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

package dev.dnpm.services.molekulargenetik;

import dev.dnpm.dto.Variant;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OsMolekulargenetikFormService implements MolekulargenetikFormService {

    /**
     * Ermittelt alle (unterstützten) Varianten zur Prozedur eines Formulars "OS.Molekulargenetik" (oder Variante)
     * Unterstützte Varianten sind:
     * <uL>
     *     <li>Einfache Variante
     *     <li>CNV
     *     <li>Fusion
     * @param procedure Die Prozedur zum Formular "OS.Molekulargenetik" (oder Variante)
     * @return Die unterstützten Varianten oder eine leere Liste, wenn keine Varianten gefunden wurden.
     */
    @Override
    public List<Variant> getVariants(Procedure procedure) {
        if (! procedureWithUsableFormVariant(procedure)) {
            return List.of();
        }

        var subforms = procedure.getSubProceduresMap().get("MolekulargenetischeUntersuchung");
        if (null == subforms) {
            return List.of();
        }

        return subforms.stream()
                .map(Variant::fromProcedure)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    private boolean procedureWithUsableFormVariant(Procedure procedure) {
        return "OS.Molekulargenetik".equals(procedure.getFormName())
                || "UKER.Molekulargenetik".equals(procedure.getFormName());
    }
}
