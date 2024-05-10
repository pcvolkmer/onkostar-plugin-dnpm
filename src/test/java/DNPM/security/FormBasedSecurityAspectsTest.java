package DNPM.security;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormBasedSecurityAspectsTest {

    private DummyClass dummyClass;

    private IOnkostarApi onkostarApi;

    private FormBasedPermissionEvaluator permissionEvaluator;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock FormBasedPermissionEvaluator permissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.permissionEvaluator = permissionEvaluator;

        // Create proxied instance of DummyClass as done within Onkostar using Spring AOP
        var dummyClass = new DummyClass(onkostarApi);
        AspectJProxyFactory factory = new AspectJProxyFactory(dummyClass);
        FormBasedSecurityAspects securityAspects = new FormBasedSecurityAspects(this.permissionEvaluator);
        factory.addAspect(securityAspects);
        this.dummyClass = factory.getProxy();
    }

    @Test
    void testShouldAllowSecuredMethodCallWithPatientParam() {
        this.dummyClass.methodWithPatientParam(new Patient(onkostarApi));
        verify(onkostarApi, times(1)).savePatient(any(Patient.class));
    }

    @Test
    void testShouldPreventSecuredMethodCallWithProcedureParam() {
        when(this.permissionEvaluator.hasPermission(any(), any(Procedure.class), any(PermissionType.class)))
                .thenReturn(false);

        var exception = assertThrows(
                Exception.class,
                () -> this.dummyClass.methodWithProcedureParam(new Procedure(onkostarApi))
        );
        assertThat(exception).isExactlyInstanceOf(IllegalSecuredObjectAccessException.class);
    }

    @Test
    void testShouldAllowSecuredMethodCallWithProcedureParam() throws Exception {
        when(this.permissionEvaluator.hasPermission(any(), any(Procedure.class), any(PermissionType.class)))
                .thenReturn(true);

        this.dummyClass.methodWithProcedureParam(new Procedure(onkostarApi));

        verify(onkostarApi, times(1)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void testShouldAllowSecuredMethodCallWithPatientReturnValue() {
        var actual = this.dummyClass.methodWithPatientReturnValue(1);
        assertThat(actual).isNotNull();
    }

    @Test
    void testShouldPreventSecuredMethodCallWithProcedureReturnValue() {
        when(this.permissionEvaluator.hasPermission(any(), any(Procedure.class), any(PermissionType.class)))
                .thenReturn(false);

        var exception = assertThrows(
                Exception.class,
                () -> this.dummyClass.methodWithProcedureReturnValue(1)
        );
        assertThat(exception).isExactlyInstanceOf(IllegalSecuredObjectAccessException.class);
    }

    @Test
    void testShouldAllowSecuredMethodCallWithProcedureReturnValue() {
        when(this.permissionEvaluator.hasPermission(any(), any(Procedure.class), any(PermissionType.class)))
                .thenReturn(true);

        var actual = this.dummyClass.methodWithProcedureReturnValue(1);

        assertThat(actual).isNotNull();
    }

    private static class DummyClass {

        private final IOnkostarApi onkostarApi;

        DummyClass(final IOnkostarApi onkostarApi) {
            this.onkostarApi = onkostarApi;
        }

        @FormSecured
        public void methodWithPatientParam(Patient patient) {
            this.onkostarApi.savePatient(patient);
        }

        @FormSecured
        public void methodWithProcedureParam(Procedure procedure) throws Exception {
            this.onkostarApi.saveProcedure(procedure, false);
        }

        @FormSecuredResult
        public Patient methodWithPatientReturnValue(int id) {
            var patient = new Patient(this.onkostarApi);
            patient.setId(id);
            return patient;
        }

        @FormSecuredResult
        public Procedure methodWithProcedureReturnValue(int id) {
            var procedure = new Procedure(this.onkostarApi);
            procedure.setId(id);
            return procedure;
        }
    }

}
