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
public class PersonPoolBasedSecurityAspects {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PersonPoolBasedPermissionEvaluator permissionEvaluator;

    public PersonPoolBasedSecurityAspects(PersonPoolBasedPermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @AfterReturning(value = "@annotation(dev.dnpm.oshelper.security.PersonPoolSecuredResult) ", returning = "patient")
    public void afterPatient(Patient patient) {
        if (
                null != patient
                        && ! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), patient, PermissionType.READ_WRITE)
        ) {
            logger.warn("Rückgabe von Patient blockiert: {}", patient.getId());
            throw new IllegalSecuredObjectAccessException();
        }
    }

    @AfterReturning(value = "@annotation(dev.dnpm.oshelper.security.PersonPoolSecuredResult)", returning = "procedure")
    public void afterProcedure(Procedure procedure) {
        if (
                null != procedure
                        && ! permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), procedure, PermissionType.READ_WRITE)
        ) {
            logger.warn("Rückgabe von Prozedur blockiert: {}", procedure.getId());
            throw new IllegalSecuredObjectAccessException();
        }
    }

    @Before(value = "@annotation(dev.dnpm.oshelper.security.PersonPoolSecured)")
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

    @Before(value = "@annotation(dev.dnpm.oshelper.security.PersonPoolSecured)")
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
