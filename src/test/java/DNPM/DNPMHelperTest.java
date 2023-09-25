package DNPM;

import DNPM.analyzer.DNPMHelper;
import DNPM.security.IllegalSecuredObjectAccessException;
import DNPM.security.PermissionType;
import DNPM.security.PersonPoolBasedPermissionEvaluator;
import DNPM.services.systemtherapie.SystemtherapieService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DNPMHelperTest {

    private IOnkostarApi onkostarApi;

    private SystemtherapieService systemtherapieService;

    private PersonPoolBasedPermissionEvaluator personPoolBasedPermissionEvaluator;

    private DNPMHelper dnpmHelper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SystemtherapieService systemtherapieService,
            @Mock PersonPoolBasedPermissionEvaluator personPoolBasedPermissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.systemtherapieService = systemtherapieService;
        this.personPoolBasedPermissionEvaluator = personPoolBasedPermissionEvaluator;
        this.dnpmHelper = new DNPMHelper(onkostarApi, systemtherapieService, personPoolBasedPermissionEvaluator);
    }

    @Test
    void testShouldRequestSystemischeTherapienFromDiagnose() {
        dnpmHelper.getSystemischeTherapienFromDiagnose(Map.of("DiagnoseId", 1234));

        var captor = ArgumentCaptor.forClass(Integer.class);
        verify(systemtherapieService, times(1)).getSystemischeTherapienFromDiagnose(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1234);
    }

    @Test
    void testShouldReturnNullIfNoDiagnoseIdCallingGetSystemischeTherapienFromDiagnose() {
        var actual = dnpmHelper.getSystemischeTherapienFromDiagnose(new HashMap<>());

        assertThat(actual).isNull();
    }

    @Test
    void testShouldReturnNullIfNoProcedureIdCallingGetEmpfehlung() {
        var actual = dnpmHelper.getEmpfehlung(new HashMap<>());

        assertThat(actual).isNull();
    }

    @Nested
    class UpdateEmpfehlungPrioTests {

        @Test
        void testShouldReturnFalseIfNoRidAndNoBdCallingUpdateEmpfehlungPrio() {
            var actual = dnpmHelper.updateEmpfehlungPrio(new HashMap<>());

            assertThat(actual).isEqualTo(false);
        }

        @Test
        void testShouldReturnFalseIfNoRidCallingUpdateEmpfehlungPrio() {
            var actual = dnpmHelper.updateEmpfehlungPrio(Map.of("bd", "2023-01-01"));

            assertThat(actual).isEqualTo(false);
        }

        @Test
        void testShouldReturnFalseIfNoBdCallingUpdateEmpfehlungPrio() {
            var actual = dnpmHelper.updateEmpfehlungPrio(Map.of("rid", 1234));

            assertThat(actual).isEqualTo(false);
        }

        @Test
        void testShouldReturnTrueIfRidAndBdPresentCallingUpdateEmpfehlungPrio() {
            var sessionFactory = mock(SessionFactory.class);
            var session = mock(Session.class);
            var query = mock(SQLQuery.class);

            when(onkostarApi.getSessionFactory()).thenReturn(sessionFactory);
            when(sessionFactory.getCurrentSession()).thenReturn(session);
            when(session.createSQLQuery(anyString())).thenReturn(query);

            var actual = dnpmHelper.updateEmpfehlungPrio(Map.of("rid", 1234, "bd", "2023-01-01"));

            assertThat(actual).isEqualTo(true);
        }

        @Test
        void testShouldCreateSqlQueryWithRidAndBdCallingUpdateEmpfehlungPrio() {
            var sessionFactory = mock(SessionFactory.class);
            var session = mock(Session.class);
            var query = mock(SQLQuery.class);

            when(onkostarApi.getSessionFactory()).thenReturn(sessionFactory);
            when(sessionFactory.getCurrentSession()).thenReturn(session);
            when(session.createSQLQuery(anyString())).thenReturn(query);

            dnpmHelper.updateEmpfehlungPrio(Map.of("rid", 1234, "bd", "2023-01-01"));

            var argumentCaptor = ArgumentCaptor.forClass(String.class);
            verify(session, times(1)).createSQLQuery(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).isEqualTo("UPDATE prozedur SET beginndatum = '2023-01-01' WHERE id = '1234' ");
        }

    }

    @Nested
    class GetProzedurenFromDiagnoseTests {
        @Test
        void testShouldReturnEmptyStringOnParamCheckIfNoDataFormParamGiven() {
            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("DiagnoseId", 1, "PatientId", 2));
            assertThat(actual).isExactlyInstanceOf(String.class).isEmpty();

            verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldReturnEmptyStringOnParamCheckIfNoDiagnoseIdParamGiven() {
            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "PatientId", 2));
            assertThat(actual).isExactlyInstanceOf(String.class).isEmpty();

            verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldReturnEmptyStringOnParamCheckIfNoPatientIdParamGiven() {
            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "DiagnoseId", 1));
            assertThat(actual).isExactlyInstanceOf(String.class).isEmpty();

            verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldRequestProceduresIfRequiredParamsGiven() {
            dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "DiagnoseId", 1, "PatientId", 2));
            verify(onkostarApi, times(1)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldNotReturnProceduresNotRelatedToDisease() {
            doAnswer(invocationOnMock -> {
                var procedure = new Procedure(onkostarApi);
                procedure.setFormName("OS.Example1");
                procedure.setId(11);
                procedure.addDiseaseId(4711);
                procedure.setValue("formfield", new Item("formfield", "Wert11"));

                return List.of(procedure);
            }).when(onkostarApi).getProceduresByPatientId(anyInt());

            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "DiagnoseId", 1, "PatientId", 2));
            assertThat(actual).isEqualTo("[]");
        }

        @Test
        void testShouldReturnProcedures() {
            doAnswer(invocationOnMock -> {
                var procedure1 = new Procedure(onkostarApi);
                procedure1.setFormName("OS.Example1");
                procedure1.setId(11);
                procedure1.addDiseaseId(1);
                procedure1.setStartDate(new Date());
                procedure1.setValue("formfield", new Item("formfield", "Wert11"));

                var procedure2 = new Procedure(onkostarApi);
                procedure2.setFormName("OS.Example2");
                procedure2.setId(21);
                procedure2.addDiseaseId(1);
                procedure2.setStartDate(new Date());
                procedure2.setValue("formfield", new Item("formfield", "Wert21"));

                var procedure3 = new Procedure(onkostarApi);
                procedure3.setFormName("OS.Example1");
                procedure3.setId(12);
                procedure3.addDiseaseId(1);
                procedure3.setStartDate(new Date());
                procedure3.setValue("formfield", new Item("formfield", "Wert12"));

                return List.of(procedure1, procedure2, procedure3);
            }).when(onkostarApi).getProceduresByPatientId(anyInt());

            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "DiagnoseId", 1, "PatientId", 2));
            assertThat(actual).contains("OS.Example1", "OS.Example2", "Wert11", "Wert21", "Wert12");
        }
    }

    @Nested
    class GetVerweiseTests {

        @Test
        void testShouldReturnEmptyArrayIfNoProcedureIdParamGiven() {
            var actual = dnpmHelper.getVerweise(Map.of("PatientId", 2));
            assertThat(actual).isNull();

            verify(onkostarApi, times(0)).getSessionFactory();
        }

        @Test
        void testShouldReturnEmptyArrayIfNoPatientIdParamGiven() {
            var actual = dnpmHelper.getVerweise(Map.of("ProcedureId", 1));
            assertThat(actual).isNull();

            verify(onkostarApi, times(0)).getSessionFactory();
        }

        @Test
        void testShouldRequestSessionFactoryIfRequiredParamsGiven() {
            dnpmHelper.getVerweise(Map.of("ProcedureId", 1, "PatientId", 2));
            verify(onkostarApi, times(1)).getSessionFactory();
        }

        @Test
        void testShouldCreateSqlQueryWithPatientId() {
            var sessionFactory = mock(SessionFactory.class);
            var session = mock(Session.class);
            var query = mock(SQLQuery.class);

            when(onkostarApi.getSessionFactory()).thenReturn(sessionFactory);
            when(sessionFactory.getCurrentSession()).thenReturn(session);
            when(session.createSQLQuery(anyString())).thenReturn(query);

            dnpmHelper.getVerweise(Map.of("ProcedureId", 1, "PatientId", 2));

            var argumentCaptor = ArgumentCaptor.forClass(String.class);
            verify(session, times(1)).createSQLQuery(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).contains("WHERE patient_id = 2 AND geloescht = 0");
        }

        @Test
        void testShouldReturnEcogStatusList() {
            when(personPoolBasedPermissionEvaluator.hasPermission(any(), any(Patient.class), any(PermissionType.class)))
                    .thenReturn(true);

            doAnswer(invocationOnMock -> {
                var id = invocationOnMock.getArgument(0, Integer.class);
                var patient = new Patient(onkostarApi);
                patient.setId(id);
                return patient;
            }).when(onkostarApi).getPatient(anyInt());

            dnpmHelper.getEcogStatus(Map.of("PatientId", 42));

            var argumentCaptor = ArgumentCaptor.forClass(Patient.class);
            verify(systemtherapieService, times(1)).ecogStatus(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).isNotNull();
            assertThat(argumentCaptor.getValue().getId()).isEqualTo(42);
        }

        @Test
        void testShouldNotReturnEcogStatusListIfNoPermissionGranted() {
            when(personPoolBasedPermissionEvaluator.hasPermission(any(), any(Patient.class), any(PermissionType.class)))
                    .thenReturn(false);

            doAnswer(invocationOnMock -> {
                var id = invocationOnMock.getArgument(0, Integer.class);
                var patient = new Patient(onkostarApi);
                patient.setId(id);
                return patient;
            }).when(onkostarApi).getPatient(anyInt());

            assertThrows(IllegalSecuredObjectAccessException.class, () -> dnpmHelper.getEcogStatus(Map.of("PatientId", 42)));
        }

    }

}
