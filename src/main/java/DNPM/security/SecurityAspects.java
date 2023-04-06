package DNPM.security;

import de.itc.onkostar.api.Patient;
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
public class SecurityAspects {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PersonPoolBasedPermissionEvaluator permissionEvaluator;

    public SecurityAspects(PersonPoolBasedPermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @AfterReturning(value = "@annotation(PersonPoolSecuredResult) ", returning = "patient")
    public void afterPatient(Patient patient) {
        if (
                null != patient
                        && ! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), patient, PermissionType.READ_WRITE)
        ) {
            logger.warn("Rückgabe von Patient blockiert: {}", patient.getId());
            throw new IllegalSecuredObjectAccessException();
        }
    }

    @AfterReturning(value = "@annotation(PersonPoolSecuredResult)", returning = "procedure")
    public void afterProcedure(Procedure procedure) {
        if (
                null != procedure
                        && ! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), procedure, PermissionType.READ_WRITE)
        ) {
            logger.warn("Rückgabe von Prozedur blockiert: {}", procedure.getId());
            throw new IllegalSecuredObjectAccessException();
        }
    }

    @Before(value = "@annotation(PersonPoolSecured)")
    public void beforePatient(JoinPoint jp) {
        Arrays.stream(jp.getArgs())
                .filter(arg -> arg instanceof Patient)
                .forEach(patient -> {
                    if (! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), patient, PermissionType.READ_WRITE)) {
                        logger.warn("Zugriff auf Patient blockiert: {}", ((Patient)patient).getId());
                        throw new IllegalSecuredObjectAccessException();
                    }
                });
    }

    @Before(value = "@annotation(PersonPoolSecured)")
    public void beforeProcedure(JoinPoint jp) {
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
