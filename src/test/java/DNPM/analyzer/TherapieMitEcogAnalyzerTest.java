package DNPM.analyzer;

import DNPM.dto.EcogStatusWithDate;
import DNPM.services.strahlentherapie.StrahlentherapieService;
import DNPM.services.systemtherapie.SystemtherapieService;
import de.itc.onkostar.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapieMitEcogAnalyzerTest {

    private IOnkostarApi onkostarApi;

    private StrahlentherapieService strahlentherapieService;

    private SystemtherapieService systemtherapieService;

    private TherapieMitEcogAnalyzer therapieMitEcogAnalyzer;

    private Disease dummyDisease(int id, Date diagnosisDate) {
        var disease = new Disease(onkostarApi);
        disease.setId(id);
        disease.setDiagnosisDate(diagnosisDate);
        return disease;
    }

    private Date daysPassed(int days) {
        return Date.from(Instant.now().minus(days, ChronoUnit.DAYS));
    }

    @BeforeEach
    void setUp(
            @Mock IOnkostarApi onkostarApi,
            @Mock StrahlentherapieService strahlentherapieService,
            @Mock SystemtherapieService systemtherapieService
    ) {
        this.onkostarApi = onkostarApi;
        this.strahlentherapieService = strahlentherapieService;
        this.systemtherapieService = systemtherapieService;
        this.therapieMitEcogAnalyzer = new TherapieMitEcogAnalyzer(onkostarApi, strahlentherapieService, systemtherapieService);
    }

    @Test
    void shouldInsertNewEcogStatus() throws Exception {
        final var diagnosisDate = daysPassed(7);
        final var ecogDate = daysPassed(1);
        final var procedureDate = daysPassed(1);

        doAnswer(invocationOnMock -> List.of(new EcogStatusWithDate(ecogDate, "0")))
                .when(systemtherapieService).ecogStatus(any(Patient.class));

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setStartDate(procedureDate);
        procedure.setEditState(ProcedureEditStateType.COMPLETED);
        procedure.setPatientId(1);
        procedure.setPatient(patient);
        procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

        doAnswer(invocationOnMock -> List.of(dummyDisease(42, diagnosisDate))).when(this.onkostarApi).getDiseasesByPatientId(anyInt());

        doAnswer(invocationOnMock -> List.of(procedure)).when(onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        therapieMitEcogAnalyzer.analyze(procedure, dummyDisease(10, diagnosisDate));

        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        var formNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(onkostarApi, times(1)).getProceduresForDiseaseByForm(idCaptor.capture(), formNameCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(42);
        assertThat(formNameCaptor.getValue()).isEqualTo("DNPM Klinik/Anamnese");

        verify(onkostarApi, times(1)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldNotModifyEcogStatusIfNoCompletedSystemTherapy() throws Exception {
        final var diagnosisDate = daysPassed(7);
        final var procedureDate = daysPassed(1);

        doAnswer(invocationOnMock -> List.of())
                .when(systemtherapieService).ecogStatus(any(Patient.class));

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setStartDate(procedureDate);
        procedure.setEditState(ProcedureEditStateType.COMPLETED);
        procedure.setPatientId(1);
        procedure.setPatient(patient);
        procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

        therapieMitEcogAnalyzer.analyze(procedure, dummyDisease(10, diagnosisDate));

        verify(onkostarApi, times(0)).getProceduresForDiseaseByForm(anyInt(), anyString());
        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldNotIncludeEcogStatusBeforeDiagnosisDate() throws Exception {
        final var diagnosisDate = daysPassed(7);
        final var ecogDate = daysPassed(28);
        final var procedureDate = daysPassed(1);

        doAnswer(invocationOnMock -> List.of(new EcogStatusWithDate(ecogDate, "0")))
                .when(systemtherapieService).ecogStatus(any(Patient.class));

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setStartDate(procedureDate);
        procedure.setEditState(ProcedureEditStateType.COMPLETED);
        procedure.setPatientId(1);
        procedure.setPatient(patient);
        procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

        therapieMitEcogAnalyzer.analyze(procedure, dummyDisease(10, diagnosisDate));

        verify(onkostarApi, times(0)).getProceduresForDiseaseByForm(anyInt(), anyString());
        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldRequestEcogFromStrahlentherapieAndSystemtherapie() {
        final var diagnosisDate = daysPassed(7);
        final var procedureDate = daysPassed(1);

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setStartDate(procedureDate);
        procedure.setEditState(ProcedureEditStateType.COMPLETED);
        procedure.setPatientId(1);
        procedure.setPatient(patient);
        procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

        therapieMitEcogAnalyzer.analyze(procedure, dummyDisease(10, diagnosisDate));

        verify(strahlentherapieService, times(1)).ecogStatus(any());
        verify(systemtherapieService, times(1)).ecogStatus(any());
    }

}
