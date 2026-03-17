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

package dev.dnpm.oshelper.services;

import dev.dnpm.oshelper.dto.EcogStatusWithDate;
import de.itc.onkostar.api.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Schnittstelle zum Ermitteln von ECOG-Statusinformationen
 *
 *  @since 0.6.0
 */
public interface TherapieMitEcogService {

    /**
     * Ermittelt den letzten bekannten ECOG-Status aus allen Therapieformularen des Patienten
     * @param patient Der zu verwendende Patient
     * @return Der ECOG-Status als String oder leeres Optional
     */
    Optional<String> latestEcogStatus(Patient patient);

    /**
     * Ermittelt jeden bekannten ECOG-Status aus allen Therapieformularen des Patienten
     * @param patient Der zu verwendende Patient
     * @return Eine Liste mit Datum und ECOG-Status als String
     */
    List<EcogStatusWithDate> ecogStatus(Patient patient);

}
