package DNPM.analyzer;

import DNPM.services.systemtherapie.SystemtherapieService;
import de.itc.onkostar.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemtherapieAnalyzerTest {

    private IOnkostarApi onkostarApi;

    private SystemtherapieService systemtherapieService;

    private SystemtherapieAnalyzer systemtherapieAnalyzer;

    @BeforeEach
    void setUp(
        @Mock IOnkostarApi onkostarApi,
        @Mock SystemtherapieService systemtherapieService
    ) {
        this.onkostarApi = onkostarApi;
        this.systemtherapieService = systemtherapieService;
        this.systemtherapieAnalyzer = new SystemtherapieAnalyzer(onkostarApi, systemtherapieService);
    }

    @Test
    void shouldInsertNewEcogStatus() throws Exception {
        doAnswer(invocationOnMock -> List.of(new SystemtherapieService.EcogStatusWithDate(new Date(), "0")))
                .when(systemtherapieService).ecogSatus(any(Patient.class));

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setStartDate(new Date());
        procedure.setEditState(ProcedureEditStateType.COMPLETED);
        procedure.setPatientId(1);
        procedure.setPatient(patient);
        procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

        doAnswer(invocationOnMock -> {
            var disease = new Disease(onkostarApi);
            disease.setId(42);
            return List.of(disease);
        }).when(this.onkostarApi).getDiseasesByPatientId(anyInt());

        doAnswer(invocationOnMock -> List.of(procedure)).when(onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        systemtherapieAnalyzer.analyze(procedure, null);

        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        var formNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(onkostarApi, times(1)).getProceduresForDiseaseByForm(idCaptor.capture(), formNameCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(42);
        assertThat(formNameCaptor.getValue()).isEqualTo("DNPM Klinik/Anamnese");

        verify(onkostarApi, times(1)).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldNotModifyEcogStatusIfNoCompletedSystemTherapy() throws Exception {
        doAnswer(invocationOnMock -> List.of())
                .when(systemtherapieService).ecogSatus(any(Patient.class));

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var procedure = new Procedure(onkostarApi);
        procedure.setId(1000);
        procedure.setStartDate(new Date());
        procedure.setEditState(ProcedureEditStateType.COMPLETED);
        procedure.setPatientId(1);
        procedure.setPatient(patient);
        procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

        systemtherapieAnalyzer.analyze(procedure, null);

        verify(onkostarApi, times(0)).getProceduresForDiseaseByForm(anyInt(), anyString());
        verify(onkostarApi, times(0)).saveProcedure(any(Procedure.class), anyBoolean());
    }

}
