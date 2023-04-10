package DNPM.security;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.PermissionEvaluator;

import javax.sql.DataSource;

public abstract class AbstractDelegatedPermissionEvaluator implements PermissionEvaluator {

    protected static final String PATIENT = Patient.class.getSimpleName();

    protected static final String PROCEDURE = Procedure.class.getSimpleName();

    protected final IOnkostarApi onkostarApi;

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractDelegatedPermissionEvaluator(final IOnkostarApi onkostarApi, final DataSource dataSource) {
        this.onkostarApi = onkostarApi;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
