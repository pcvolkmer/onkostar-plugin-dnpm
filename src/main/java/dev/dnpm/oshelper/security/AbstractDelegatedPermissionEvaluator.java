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

package dev.dnpm.oshelper.security;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.springframework.security.access.PermissionEvaluator;

public abstract class AbstractDelegatedPermissionEvaluator implements PermissionEvaluator {

    protected static final String PATIENT = Patient.class.getSimpleName();

    protected static final String PROCEDURE = Procedure.class.getSimpleName();

    protected final IOnkostarApi onkostarApi;

    protected final SecurityService securityService;

    protected AbstractDelegatedPermissionEvaluator(final IOnkostarApi onkostarApi, final SecurityService securityService) {
        this.onkostarApi = onkostarApi;
        this.securityService = securityService;
    }

}
