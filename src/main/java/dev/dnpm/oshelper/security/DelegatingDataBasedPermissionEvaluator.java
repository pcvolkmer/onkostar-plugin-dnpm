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

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * PermissionEvaluator zur Gesamtprüfung der Zugriffsberechtigung.
 * Die konkrete Berechtigungsprüfung wird an die nachgelagerten PermissionEvaluatoren delegiert,
 * welche jeweils einzeln dem Zugriff zustimmen müssen.
 */
@Component
public class DelegatingDataBasedPermissionEvaluator implements PermissionEvaluator {

    private final List<AbstractDelegatedPermissionEvaluator> permissionEvaluators;

    public DelegatingDataBasedPermissionEvaluator(final List<AbstractDelegatedPermissionEvaluator> permissionEvaluators) {
        this.permissionEvaluators = permissionEvaluators;
    }

    /**
     * Auswertung der Zugriffsberechtigung für authentifizierten Benutzer auf Zielobjekt mit angeforderter Berechtigung.
     * Hierbei wird die Berechtigungsprüfung an alle nachgelagerten PermissionEvaluatoren delegiert.
     * Alle müssen dem Zugriff zustimmen.
     *
     * @param authentication Das Authentication Objekt
     * @param targetObject Das Zielobjekt
     * @param permissionType Die angeforderte Berechtigung
     * @return Gibt <code>true</code> zurück, wenn der Benutzer die Berechtigung hat
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permissionType) {
        return permissionEvaluators.stream()
                .allMatch(permissionEvaluator -> permissionEvaluator.hasPermission(authentication, targetObject, permissionType));
    }

    /**
     * Auswertung anhand der ID und des Namens des Zielobjekts.
     * Hierbei wird die Berechtigungsprüfung an alle nachgelagerten PermissionEvaluatoren delegiert.
     * Alle müssen dem Zugriff zustimmen.
     *
     * @param authentication Authentication-Object
     * @param targetId ID des Objekts
     * @param targetType Name der Zielobjektklasse
     * @param permissionType Die angeforderte Berechtigung
     * @return Gibt <code>true</code> zurück, wenn der Benutzer die Berechtigung hat
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permissionType) {
        return permissionEvaluators.stream()
                .allMatch(permissionEvaluator -> permissionEvaluator.hasPermission(authentication, targetId, targetType, permissionType));
    }
}
