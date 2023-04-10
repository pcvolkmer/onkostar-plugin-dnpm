package DNPM.security;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

/**
 * Permission-Evaluator zur Auswertung der Berechtigung auf Objekte aufgrund der Formularberechtigung
 */
@Component
public class FormBasedPermissionEvaluator implements PermissionEvaluator {

    private final IOnkostarApi onkostarApi;

    private final JdbcTemplate jdbcTemplate;

    public FormBasedPermissionEvaluator(final IOnkostarApi onkostarApi, final DataSource dataSource) {
        this.onkostarApi = onkostarApi;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
        if (permissionType instanceof PermissionType) {
            if (targetObject instanceof Patient) {
                return true;
            } else if (targetObject instanceof Procedure) {
                return getFormNamesForPermission(authentication, (PermissionType)permissionType)
                        .contains(((Procedure)targetObject).getFormName());
            }
        }
        return false;
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
        if (targetId instanceof Integer) {
            if ("Patient".equals(targetType)) {
                return true;
            }
            var procedure = this.onkostarApi.getProcedure((int)targetId);
            if (null != procedure) {
                return getFormNamesForPermission(authentication, (PermissionType) permissionType).contains(procedure.getFormName());
            }
        }
        return false;
    }

    List<String> getFormNamesForPermission(Authentication authentication, PermissionType permissionType) {

        var sql = "SELECT df.name FROM formular_usergroup_zugriff " +
                " JOIN data_form df ON formular_usergroup_zugriff.formular_id = df.id " +
                " JOIN usergroup u ON formular_usergroup_zugriff.usergroup_id = u.id " +
                " JOIN akteur_usergroup au ON u.id = au.usergroup_id " +
                " JOIN akteur a on au.akteur_id = a.id " +
                " WHERE a.login = ? AND a.aktiv AND a.anmelden_moeglich ";

        if (PermissionType.READ_WRITE == permissionType) {
            sql += " AND formular_usergroup_zugriff.bearbeiten ";
        }

        var userDetails = (UserDetails)authentication.getPrincipal();

        return jdbcTemplate
                .query(sql, new Object[]{userDetails.getUsername()}, (rs, rowNum) -> rs.getString("name"));
    }


}
