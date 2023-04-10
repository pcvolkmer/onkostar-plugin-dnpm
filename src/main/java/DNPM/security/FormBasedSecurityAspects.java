package DNPM.security;

import de.itc.onkostar.api.Procedure;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class FormBasedSecurityAspects {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FormBasedPermissionEvaluator permissionEvaluator;

    public FormBasedSecurityAspects(
            final FormBasedPermissionEvaluator permissionEvaluator
            ) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @AfterReturning(value = "@annotation(FormSecuredResult)", returning = "procedure")
    public void afterProcedureFormBased(Procedure procedure) {
        if (
                null != procedure
                        && ! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), procedure, PermissionType.READ_WRITE)
        ) {
            logger.warn("Rückgabe von Prozedur blockiert: {}", procedure.getId());
            throw new IllegalSecuredObjectAccessException();
        }
    }

    @Before(value = "@annotation(FormSecured)")
    public void beforeProcedureFormBased(JoinPoint jp) {
        Arrays.stream(jp.getArgs())
                .filter(arg -> arg instanceof Procedure)
                .forEach(procedure -> {
                    if (! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), procedure, PermissionType.READ_WRITE)) {
                        logger.warn("Zugriff auf Prozedur blockiert: {}", ((Procedure)procedure).getId());
                        throw new IllegalSecuredObjectAccessException();
                    }
                });
    }
}
