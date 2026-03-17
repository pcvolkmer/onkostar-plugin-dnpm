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

package dev.dnpm.oshelper.analyzer;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.OnkostarPluginType;

public abstract class BackendService implements IPluginPart {

    @Override
    public final OnkostarPluginType getType() {
        return OnkostarPluginType.BACKEND_SERVICE;
    }

    /**
     * Ein Backend-Service verwendet die Methode nicht, daher wird hier eine final Stub-Implementierung
     * verwendet, die ein Überschreiben verhindert.
     * @param procedure
     * @param disease
     */
    @Override
    public final void analyze(Procedure procedure, Disease disease) {
        // No op
    }

}
