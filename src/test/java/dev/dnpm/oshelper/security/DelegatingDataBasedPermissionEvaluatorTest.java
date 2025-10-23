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