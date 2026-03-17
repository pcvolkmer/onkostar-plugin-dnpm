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

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DelegatingDataBasedPermissionEvaluatorTest {

    private IOnkostarApi onkostarApi;

    private PersonPoolBasedPermissionEvaluator personPoolBasedPermissionEvaluator;

    private FormBasedPermissionEvaluator formBasedPermissionEvaluator;

    private DelegatingDataBasedPermissionEvaluator delegatingDataBasedPermissionEvaluator;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock PersonPoolBasedPermissionEvaluator personPoolBasedPermissionEvaluator,
            @Mock FormBasedPermissionEvaluator formBasedPermissionEvaluator
            ) {
        this.onkostarApi = onkostarApi;
        this.personPoolBasedPermissionEvaluator = personPoolBasedPermissionEvaluator;
        this.formBasedPermissionEvaluator = formBasedPermissionEvaluator;

        this.delegatingDataBasedPermissionEvaluator = new DelegatingDataBasedPermissionEvaluator(
                List.of(personPoolBasedPermissionEvaluator, formBasedPermissionEvaluator)
        );
    }

    @Test
    void testShouldGrantPermissionIfAllDelegatedPermissionEvaluatorsGrantsAccessByObject() {
        when(personPoolBasedPermissionEvaluator.hasPermission(any(), any(Patient.class), any(PermissionType.class))).thenReturn(true);
        when(formBasedPermissionEvaluator.hasPermission(any(), any(Patient.class), any(PermissionType.class))).thenReturn(true);

        var actual = delegatingDataBasedPermissionEvaluator.hasPermission(new DummyAuthentication(), new Patient(this.onkostarApi), PermissionType.READ);

        assertThat(actual).isTrue();
    }

    @Test
    void testShouldGrantPermissionIfAllDelegatedPermissionEvaluatorsGrantsAccessByIdAndType() {
        when(personPoolBasedPermissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class))).thenReturn(true);
        when(formBasedPermissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class))).thenReturn(true);

        var actual = delegatingDataBasedPermissionEvaluator.hasPermission(new DummyAuthentication(), 123, "Patient", PermissionType.READ);

        assertThat(actual).isTrue();
    }

    @Test
    void testShouldDenyPermissionIfAtLeastOneDelegatedPermissionEvaluatorsDeniesAccessByObject() {
        when(personPoolBasedPermissionEvaluator.hasPermission(any(), any(Patient.class), any(PermissionType.class))).thenReturn(true);
        when(formBasedPermissionEvaluator.hasPermission(any(), any(Patient.class), any(PermissionType.class))).thenReturn(false);

        var actual = delegatingDataBasedPermissionEvaluator.hasPermission(new DummyAuthentication(), new Patient(this.onkostarApi), PermissionType.READ);

        assertThat(actual).isFalse();
    }

    @Test
    void testShouldDenyPermissionIfAtLeastOneDelegatedPermissionEvaluatorsDeniesAccessByIdAndType() {
        when(personPoolBasedPermissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class))).thenReturn(false);

        var actual = delegatingDataBasedPermissionEvaluator.hasPermission(new DummyAuthentication(), 123, "Patient", PermissionType.READ);

        assertThat(actual).isFalse();
    }

}

class DummyAuthentication implements Authentication {
    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {

    }
}