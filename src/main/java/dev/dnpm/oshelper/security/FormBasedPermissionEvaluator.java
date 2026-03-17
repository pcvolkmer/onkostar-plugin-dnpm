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
import de.itc.onkostar.api.Procedure;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Permission-Evaluator zur Auswertung der Berechtigung auf Objekte aufgrund der Formularberechtigung
 */
@Component
public class FormBasedPermissionEvaluator extends AbstractDelegatedPermissionEvaluator {

    public FormBasedPermissionEvaluator(final IOnkostarApi onkostarApi, final SecurityService securityService) {
        super(onkostarApi, securityService);
    }

    /**
     * Auswertung der Zugriffsberechtigung für authentifizierten Benutzer auf Zielobjekt mit angeforderter Berechtigung.
     * Zugriff auf Objekte vom Typ "Patient" wird immer gewährt.
     *
     * @param authentication Das Authentication Objekt
     * @param targetObject Das Zielobjekt
     * @param permissionType Die angeforderte Berechtigung
     * @return Gibt <code>true</code> zurück, wenn der Benutzer die Berechtigung hat
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permissionType) {
        if (permissionType instanceof PermissionType && targetObject instanceof Procedure) {
            return this.securityService.getFormNamesForPermission(authentication, (PermissionType)permissionType)
                    .contains(((Procedure)targetObject).getFormName());
        }
        return true;
    }

    /**
     * Auswertung anhand der ID und des Namens des Zielobjekts.
     * Zugriff auf Objekte vom Typ "Patient" wird immer gewährt.
     *
     * @param authentication Authentication-Object
     * @param targetId ID des Objekts
     * @param targetType Name der Zielobjektklasse
     * @param permissionType Die angeforderte Berechtigung
     * @return Gibt <code>true</code> zurück, wenn der Benutzer die Berechtigung hat
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permissionType) {
        if (permissionType instanceof PermissionType && targetId instanceof Integer && PROCEDURE.equals(targetType)) {
            var procedure = this.onkostarApi.getProcedure((int)targetId);
            if (null != procedure) {
                return this.securityService.getFormNamesForPermission(authentication, (PermissionType) permissionType).contains(procedure.getFormName());
            }
        }
        return true;
    }

}
