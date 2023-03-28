package DNPM;

import DNPM.services.systemtherapie.SystemtherapieService;
import de.itc.onkostar.api.IOnkostarApi;
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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DNPMHelperTest {

    private IOnkostarApi onkostarApi;

    private SystemtherapieService systemtherapieService;

    private DNPMHelper dnpmHelper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SystemtherapieService systemtherapieService
    ) {
        this.onkostarApi = onkostarApi;
        this.systemtherapieService = systemtherapieService;
        this.dnpmHelper = new DNPMHelper(onkostarApi, systemtherapieService);
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
            assertThat(actual).isEqualTo("");

            verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldReturnEmptyStringOnParamCheckIfNoDiagnoseIdParamGiven() {
            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "PatientId", 2));
            assertThat(actual).isEqualTo("");

            verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldReturnEmptyStringOnParamCheckIfNoPatientIdParamGiven() {
            var actual = dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "DiagnoseId", 1));
            assertThat(actual).isEqualTo("");

            verify(onkostarApi, times(0)).getProceduresByPatientId(anyInt());
        }

        @Test
        void testShouldRequestProceduresIfRequiredParamsGiven() {
            dnpmHelper.getProzedurenFromDiagnose(Map.of("dataForm", "OS.Example", "DiagnoseId", 1, "PatientId", 2));
            verify(onkostarApi, times(1)).getProceduresByPatientId(anyInt());
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

    }

}
