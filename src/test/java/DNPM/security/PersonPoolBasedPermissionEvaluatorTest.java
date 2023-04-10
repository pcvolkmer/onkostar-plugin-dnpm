package DNPM.security;

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
class PersonPoolBasedPermissionEvaluatorTest {

    private IOnkostarApi onkostarApi;

    private Authentication dummyAuthentication;

    private PersonPoolBasedPermissionEvaluator permissionEvaluator;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SecurityService securityService,
            @Mock DummyAuthentication dummyAuthentication
            ) {
        this.onkostarApi = onkostarApi;
        this.dummyAuthentication = dummyAuthentication;

        this.permissionEvaluator = new PersonPoolBasedPermissionEvaluator(
                onkostarApi, securityService
        );

        when(securityService.getPersonPoolIdsForPermission(any(Authentication.class), any(PermissionType.class))).thenReturn(List.of("Pool2", "Pool3", "Pool5"));
    }

    @Test
    void testShouldGrantPermissionByPatientObject() {
        var object = new Patient(onkostarApi);
        object.setPersonPoolCode("Pool2");

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);

        assertThat(actual).isTrue();
    }

    @Test
    void testShouldGrantPermissionByPatientIdAndType() {
        doAnswer(invocationOnMock -> {
            var object = new Patient(onkostarApi);
            object.setPersonPoolCode("Pool2");
            return object;
        }).when(onkostarApi).getPatient(anyInt());

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 123, PersonPoolBasedPermissionEvaluator.PATIENT, PermissionType.READ);

        assertThat(actual).isTrue();
    }

    @Test
    void testShouldDenyPermissionByPatientObject() {
        var object = new Patient(onkostarApi);
        object.setPersonPoolCode("Pool1");

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);

        assertThat(actual).isFalse();
    }

    @Test
    void testShouldDenyPermissionByPatientIdAndType() {
        doAnswer(invocationOnMock -> {
            var object = new Patient(onkostarApi);
            object.setPersonPoolCode("Pool1");
            return object;
        }).when(onkostarApi).getPatient(anyInt());

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 123, PersonPoolBasedPermissionEvaluator.PATIENT, PermissionType.READ);

        assertThat(actual).isFalse();
    }

    @Test
    void testShouldGrantPermissionByProcedureObject() {
        var patient = new Patient(onkostarApi);
        patient.setPersonPoolCode("Pool2");

        var object = new Procedure(onkostarApi);
        object.setFormName("OS.Form1");
        object.setPatient(patient);

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);

        assertThat(actual).isTrue();
    }

    @Test
    void testShouldGrantPermissionByProcedureIdAndType() {
        doAnswer(invocationOnMock -> {
            var patient = new Patient(onkostarApi);
            patient.setPersonPoolCode("Pool2");

            var object = new Procedure(onkostarApi);
            object.setFormName("OS.Form1");
            object.setPatient(patient);

            return object;
        }).when(onkostarApi).getProcedure(anyInt());

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 456, PersonPoolBasedPermissionEvaluator.PROCEDURE, PermissionType.READ);

        assertThat(actual).isTrue();
    }

    @Test
    void testShouldDenyPermissionByProcedureObject() {
        var patient = new Patient(onkostarApi);
        patient.setPersonPoolCode("Pool1");

        var object = new Procedure(onkostarApi);
        object.setFormName("OS.Form1");
        object.setPatient(patient);

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, object, PermissionType.READ);

        assertThat(actual).isFalse();
    }

    @Test
    void testShouldDenyPermissionByProcedureIdAndType() {
        doAnswer(invocationOnMock -> {
            var patient = new Patient(onkostarApi);
            patient.setPersonPoolCode("Pool1");

            var object = new Procedure(onkostarApi);
            object.setFormName("OS.Form1");
            object.setPatient(patient);

            return object;
        }).when(onkostarApi).getProcedure(anyInt());

        var actual = permissionEvaluator.hasPermission(this.dummyAuthentication, 123, PersonPoolBasedPermissionEvaluator.PROCEDURE, PermissionType.READ);

        assertThat(actual).isFalse();
    }

}