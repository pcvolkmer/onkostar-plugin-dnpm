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

import dev.dnpm.oshelper.dto.Studie;

import java.util.List;

public interface StudienService {

    /**
     * Übergibt eine Liste mit allen Studien
     *
     * @return Liste mit allen Studien
     */
    List<Studie> findAll();

    /**
     * Übergibt eine Liste mit Studien, deren (Kurz-)Beschreibung oder Studiennummer den übergebenen Wert enthalten
     *
     * @param query Wert der enthalten sein muss
     * @return Gefilterte Liste mit Studien
     */
    List<Studie> findByQuery(String query);

    /**
     * Übergibt eine Liste mit aktiven Studien
     *
     * @return Liste mit aktiven Studien
     */
    List<Studie> findActive();

    /**
     * Übergibt eine Liste mit aktiven Studien, deren (Kurz-)Beschreibung oder Studiennummer den übergebenen Wert enthalten
     *
     * @param query Wert der enthalten sein muss
     * @return Gefilterte Liste mit aktiven Studien
     */
    List<Studie> findActiveByQuery(String query);

}
