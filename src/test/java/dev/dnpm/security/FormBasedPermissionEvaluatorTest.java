package dev.dnpm.security;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormBasedPermissionEvaluatorTest {

    private IOnkostarApi onkostarApi;

    private Authentication dummyAuthentication;

    private SecurityService securityService;

    private FormBasedPermissionEvaluator permissionEvaluator;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SecurityService securityService,
            @Mock DummyAuthentication dummyAuthentication
    ) {
        this.onkostarApi = onkostarApi;
        this.dummyAuthentication = dummyAuthentication;
        this.securityService = securityService;

        this.permissionEvaluator = new FormBasedPermissionEvaluator(
                onkostarApi, securityService
        );
    }

    @Test
    void testShouldGrantPermissionByProcedure() {
        when(securityService.getFormNamesForPermission(any(Authentication.class), any(PermissionType.class))).thenReturn(List.of("OS.Form2", "OS.Form3", "OS.Form5"));

        var object = new Procedure(onkostarApi);
        object.setFormName("OS.Form2");

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);
        assertThat(actual).isTrue();
    }

    @Test
    void testShouldGrantPermissionByProcedureId() {
        when(securityService.getFormNamesForPermission(any(Authentication.class), any(PermissionType.class))).thenReturn(List.of("OS.Form2", "OS.Form3", "OS.Form5"));

        doAnswer(invocationOnMock -> {
            var object = new Procedure(onkostarApi);
            object.setFormName("OS.Form2");
            return object;
        }).when(onkostarApi).getProcedure(anyInt());

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 123, PersonPoolBasedPermissionEvaluator.PROCEDURE, PermissionType.READ);
        assertThat(actual).isTrue();
    }

    @Test
    void testShouldDenyPermissionByProcedure() {
        when(securityService.getFormNamesForPermission(any(Authentication.class), any(PermissionType.class))).thenReturn(List.of("OS.Form2", "OS.Form3", "OS.Form5"));

        var object = new Procedure(onkostarApi);
        object.setFormName("OS.Form1");

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);
        assertThat(actual).isFalse();
    }

    @Test
    void testShouldDenyPermissionByProcedureId() {
        when(securityService.getFormNamesForPermission(any(Authentication.class), any(PermissionType.class))).thenReturn(List.of("OS.Form2", "OS.Form3", "OS.Form5"));

        doAnswer(invocationOnMock -> {
            var object = new Procedure(onkostarApi);
            object.setFormName("OS.Form1");
            return object;
        }).when(onkostarApi).getProcedure(anyInt());

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 123, PersonPoolBasedPermissionEvaluator.PROCEDURE, PermissionType.READ);
        assertThat(actual).isFalse();
    }

    @Test
    void testShouldVoteForPermissionToPatient() {
        var object = new Patient(onkostarApi);
        object.setPersonPoolCode("Pool1");

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);
        assertThat(actual).isTrue();
    }

    @Test
    void testShouldVoteForPermissionToIdOfTypeProcedure() {
        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 123, FormBasedPermissionEvaluator.PATIENT, PermissionType.READ);
        assertThat(actual).isTrue();
    }

}
