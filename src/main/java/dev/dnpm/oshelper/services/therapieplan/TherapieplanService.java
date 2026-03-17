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

package dev.dnpm.oshelper.services.therapieplan;

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
