package dev.dnpm.oshelper.services.strahlentherapie;

import dev.dnpm.oshelper.services.SettingsService;
import de.itc.onkostar.api.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultStrahlentherapieServiceTest {

    private IOnkostarApi onkostarApi;

    private SettingsService settingsService;

    private DefaultStrahlentherapieService service;

    @BeforeEach
    void setup(@Mock IOnkostarApi onkostarApi, @Mock SettingsService settingsService) {
        this.onkostarApi = onkostarApi;
        this.settingsService = settingsService;
        this.service = new DefaultStrahlentherapieService(onkostarApi, settingsService);
    }

    @Test
    void testShouldRequestProceduresWithDefaultFormName() {
        when(this.settingsService.getSetting(anyString())).thenReturn(Optional.empty());

        doAnswer(invocationOnMock -> {
            var procedure = new Procedure(onkostarApi);
            procedure.setId(1);
            procedure.setFormName("OS.Strahlentherapie");
            procedure.setStartDate(Date.from(Instant.parse("2023-07-01T06:00:00Z")));
            procedure.setEditState(ProcedureEditStateType.COMPLETED);
            procedure.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));
            return Lists.list(procedure);
        }).when(this.onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        doAnswer(invocationOnMock -> {
            var disease = new Disease(onkostarApi);
            disease.setId(1);
            disease.setPatientId(123);
            return Lists.list(disease);
        }).when(this.onkostarApi).getDiseasesByPatientId(anyInt());

        var patient = new Patient(onkostarApi);
        patient.setId(123);

        service.ecogStatus(patient);

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(onkostarApi, times(1)).getProceduresForDiseaseByForm(anyInt(), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo("OS.Strahlentherapie");
    }

    @Test
    void testShouldReturnListOfEcogStatusWithDate() {
        doAnswer(invocationOnMock -> {
            var disease = new Disease(onkostarApi);
            disease.setId(1);
            return List.of(disease);
        }).when(this.onkostarApi).getDiseasesByPatientId(anyInt());

        doAnswer(invocationOnMock -> {
            var procedure1 = new Procedure(onkostarApi);
            procedure1.setId(1);
            procedure1.setFormName("OS.Strahlentherapie");
            procedure1.setStartDate(Date.from(Instant.parse("2023-07-01T06:00:00Z")));
            procedure1.setEditState(ProcedureEditStateType.COMPLETED);
            procedure1.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

            var procedure2 = new Procedure(onkostarApi);
            procedure2.setId(2);
            procedure2.setFormName("OS.Strahlentherapie");
            procedure2.setStartDate(Date.from(Instant.parse("2023-07-12T06:00:00Z")));
            procedure2.setEditState(ProcedureEditStateType.COMPLETED);
            procedure2.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 2));
            return List.of(procedure1, procedure2);
        }).when(this.onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var actual = service.ecogStatus(patient);

        assertThat(actual)
                .isNotNull()
                .isExactlyInstanceOf(ArrayList.class)
                .hasSize(2);
    }
}
