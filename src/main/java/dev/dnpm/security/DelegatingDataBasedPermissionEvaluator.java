package dev.dnpm.security;

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
