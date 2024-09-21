package dev.dnpm.security;

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
