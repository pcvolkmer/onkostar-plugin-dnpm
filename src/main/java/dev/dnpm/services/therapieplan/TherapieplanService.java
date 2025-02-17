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

package dev.dnpm.services.therapieplan;

import de.itc.onkostar.api.Procedure;

import java.util.List;

public interface TherapieplanService {

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in weiteren Bereichen
     * "Humangenetische Beratung" und "Reevaluation" und Unterformularen, wenn erforderlich.
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    void updateRequiredMtbEntries(Procedure procedure);

    /**
     * Finde verlinkte MTBs in Hauptformular und Unterformularen
     *
     * @param procedure Die Prozedur mit Hauptformular
     * @return Liste mit verlinkten MTBs
     */
    List<Procedure> findReferencedMtbs(Procedure procedure);

    /**
     * Finde verlinkte MTBs in Hauptformular und Unterformularen
     *
     * @param procedureId ID der Prozedur mit Hauptformular
     * @return Liste mit verlinkten MTBs
     */
    List<Procedure> findReferencedMtbs(int procedureId);

    /**
     * Finde verlinkte FollowUps für DNPM UF Einzelempfehlung
     *
     * @param procedure Die DNPM UF Einzelempfehlung Prozedur
     * @return Liste mit verlinkten FollowUps
     */
    List<Procedure> findReferencedFollowUpsForSubform(Procedure procedure);

    /**
     * Finde verlinkte FollowUps für DNPM UF Einzelempfehlung
     *
     * @param procedureId ID der Prozedur
     * @return Liste mit verlinkten FollowUps
     */
    List<Procedure> findReferencedFollowUpsForSubform(int procedureId);

}
