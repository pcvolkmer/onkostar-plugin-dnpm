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

package dev.dnpm.oshelper.services.systemtherapie;

import dev.dnpm.oshelper.services.TherapieMitEcogService;
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
