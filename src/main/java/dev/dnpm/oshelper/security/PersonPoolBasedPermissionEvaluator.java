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

package dev.dnpm.oshelper.security;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Permission-Evaluator zur Auswertung der Berechtigung auf Objekte aufgrund der Personenstammberechtigung
 */
@Component
public class PersonPoolBasedPermissionEvaluator extends AbstractDelegatedPermissionEvaluator {

    public PersonPoolBasedPermissionEvaluator(final IOnkostarApi onkostarApi, final SecurityService securityService) {
        super(onkostarApi, securityService);
    }

    /**
     * Auswertung der Zugriffsberechtigung für authentifizierten Benutzer auf Zielobjekt mit angeforderter Berechtigung.
     * @param authentication Das Authentication Objekt
     * @param targetObject Das Zielobjekt
     * @param permissionType Die angeforderte Berechtigung
     * @return Gibt <code>true</code> zurück, wenn der Benutzer die Berechtigung hat
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permissionType) {
        if (permissionType instanceof PermissionType) {
            if (targetObject instanceof Patient) {
                return this.securityService.getPersonPoolIdsForPermission(authentication, (PermissionType)permissionType)
                        .contains(((Patient)targetObject).getPersonPoolCode());
            } else if (targetObject instanceof Procedure) {
                return this.securityService.getPersonPoolIdsForPermission(authentication, (PermissionType)permissionType)
                        .contains(((Procedure)targetObject).getPatient().getPersonPoolCode());
            }
        }
        return false;
    }

    /**
     * Auswertung anhand der ID und des Namens des Zielobjekts.
     * @param authentication Authentication-Object
     * @param targetId ID des Objekts
     * @param targetType Name der Zielobjektklasse
     * @param permissionType Die angeforderte Berechtigung
     * @return Gibt <code>true</code> zurück, wenn der Benutzer die Berechtigung hat
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permissionType) {
        if (targetId instanceof Integer && permissionType instanceof PermissionType) {
            var personPoolCode = getPersonPoolCode((int)targetId, targetType);
            if (null != personPoolCode) {
                return this.securityService.getPersonPoolIdsForPermission(authentication, (PermissionType) permissionType).contains(personPoolCode);
            }
        }
        return false;
    }

    private String getPersonPoolCode(int id, String type) {
        Patient patient = null;

        if (PATIENT.equals(type)) {
            patient = onkostarApi.getPatient(id);
        } else if (PROCEDURE.equals(type)) {
            var procedure = onkostarApi.getProcedure(id);
            if (null != procedure) {
                patient = procedure.getPatient();
            }
        }

        if (null != patient) {
            return patient.getPersonPoolCode();
        }

        return null;
    }


}
