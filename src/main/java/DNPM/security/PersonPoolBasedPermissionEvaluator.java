package DNPM.security;

import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Permission-Evaluator zur Auswertung der Berechtigung auf Objekte aufgrund der Personenstammberechtigung
 */
public class PersonPoolBasedPermissionEvaluator implements PermissionEvaluator {

    private final JdbcTemplate jdbcTemplate;

    public PersonPoolBasedPermissionEvaluator(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
                return getPersonPoolIdsForPermission(authentication, (PermissionType)permissionType)
                        .contains(((Patient)targetObject).getPersonPoolCode());
            } else if (targetObject instanceof Procedure) {
                return getPersonPoolIdsForPermission(authentication, (PermissionType)permissionType)
                        .contains(((Procedure)targetObject).getPatient().getPersonPoolCode());
            }
        }
        return false;
    }

    /**
     * Auswertung nicht anhand der ID möglich. Gibt immer <code>false</code> zurück.
     * @param authentication Authentication-Object
     * @param targetId ID des Objekts
     * @param s
     * @param o
     * @return Gibt immer <code>false</code> zurück
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String s, Object o) {
        return false;
    }

    private List<String> getPersonPoolIdsForPermission(Authentication authentication, PermissionType permissionType) {
        var sql = "SELECT p.kennung FROM personenstamm_zugriff " +
                " JOIN usergroup u ON personenstamm_zugriff.benutzergruppe_id = u.id " +
                " JOIN akteur_usergroup au ON u.id = au.usergroup_id " +
                " JOIN akteur a ON au.akteur_id = a.id " +
                " JOIN personenstamm p on personenstamm_zugriff.personenstamm_id = p.id " +
                " WHERE a.login = ? AND a.aktiv AND a.anmelden_moeglich ";

        if (PermissionType.WRITE == permissionType) {
            sql += " AND personenstamm_zugriff.bearbeiten ";
        }

        var userDetails = (UserDetails)authentication.getPrincipal();

        return jdbcTemplate
                .query(sql, new Object[]{userDetails.getUsername()}, (rs, rowNum) -> rs.getString("id"));
    }


}
