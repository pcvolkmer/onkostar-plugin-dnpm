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

package dev.dnpm.oshelper.services.molekulargenetik;

import dev.dnpm.oshelper.dto.Variant;
import de.itc.onkostar.api.Procedure;

import java.util.List;

/**
 * Schnittstellenbeschreibung für Methoden zum Formular "OS.Molekulargenetik"
 */
public interface MolekulargenetikFormService {

    /**
     * Ermittelt alle (unterstützten) Varianten zur Prozedur eines Formulars "OS.Molekulargenetik"
     * @param procedure Die Prozedur zum Formular "OS.Molekulargenetik"
     * @return Die unterstützten Varianten oder eine leere Liste, wenn keine Varianten gefunden wurden.
     */
    List<Variant> getVariants(Procedure procedure);

}
