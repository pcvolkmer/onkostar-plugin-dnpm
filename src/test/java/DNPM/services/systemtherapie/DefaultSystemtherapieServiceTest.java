package DNPM.services.systemtherapie;

import DNPM.services.SettingsService;
import de.itc.onkostar.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultSystemtherapieServiceTest {

    private IOnkostarApi onkostarApi;

    private SettingsService settingsService;

    private DefaultSystemtherapieService service;

    @BeforeEach
    void setup(@Mock IOnkostarApi onkostarApi, @Mock SettingsService settingsService) {
        this.onkostarApi = onkostarApi;
        this.settingsService = settingsService;
        this.service = new DefaultSystemtherapieService(onkostarApi, settingsService);
    }

    private static Set<Map.Entry<String, Class<OsSystemischeTherapieToProzedurwerteMapper>>> expectedMapperMappings() {
        return Map.ofEntries(Map.entry("OS.Systemische Therapie", OsSystemischeTherapieToProzedurwerteMapper.class), Map.entry("OS.Systemische Therapie.VarianteUKW", OsSystemischeTherapieToProzedurwerteMapper.class)).entrySet();
    }

    @ParameterizedTest
    @MethodSource("expectedMapperMappings")
    void testShouldMapFormNameToMapper(Map.Entry<String, Class<?>> expectedMapping) {
        var procedure = new Procedure(onkostarApi);
        procedure.setFormName(expectedMapping.getKey());

        var actual = service.prozedurToProzedurwerteMapper(procedure);

        assertThat(actual).isExactlyInstanceOf(expectedMapping.getValue());
    }

    private static List<String> formnameSetting() {
        return List.of("OS.Systemische Therapie", "OS.Systemische Therapie.VarianteUKW");
    }

    @ParameterizedTest
    @MethodSource("formnameSetting")
    void testShouldRequestProceduresWithExpectedFormName(String expectedFormName) {
        when(this.settingsService.getSetting(anyString())).thenReturn(Optional.of(expectedFormName));
        when(this.onkostarApi.getProceduresForDiseaseByForm(anyInt(), anyString())).thenReturn(List.of());

        service.getSystemischeTherapienFromDiagnose(123);

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(onkostarApi, times(1)).getProceduresForDiseaseByForm(anyInt(), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(expectedFormName);
    }

    @Test
    void testShouldRequestProceduresWithDefaultFormName() {
        when(this.settingsService.getSetting(anyString())).thenReturn(Optional.empty());
        when(this.onkostarApi.getProceduresForDiseaseByForm(anyInt(), anyString())).thenReturn(List.of());

        service.getSystemischeTherapienFromDiagnose(123);

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(onkostarApi, times(1)).getProceduresForDiseaseByForm(anyInt(), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo("OS.Systemische Therapie");
    }

    @Test
    void testShouldReturnSystemischeTherapienFromDiagnose() {
        doAnswer(invocationOnMock -> {
            var procedure = new Procedure(onkostarApi);
            procedure.setFormName("OS.Systemische Therapie");
            return List.of(procedure);
        }).when(this.onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        var actual = service.getSystemischeTherapienFromDiagnose(1);

        assertThat(actual)
                .isNotNull()
                .isExactlyInstanceOf(ArrayList.class)
                .hasSize(1);
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
            procedure1.setFormName("OS.Systemische Therapie");
            procedure1.setStartDate(Date.from(Instant.parse("2023-07-01T06:00:00Z")));
            procedure1.setEditState(ProcedureEditStateType.COMPLETED);
            procedure1.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 1));

            var procedure2 = new Procedure(onkostarApi);
            procedure2.setId(2);
            procedure2.setFormName("OS.Systemische Therapie");
            procedure2.setStartDate(Date.from(Instant.parse("2023-07-12T06:00:00Z")));
            procedure2.setEditState(ProcedureEditStateType.COMPLETED);
            procedure2.setValue("ECOGvorTherapie", new Item("ECOGvorTherapie", 2));
            return List.of(procedure1, procedure2);
        }).when(this.onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        var patient = new Patient(onkostarApi);
        patient.setId(1);

        var actual = service.ecogSatus(patient);

        assertThat(actual)
                .isNotNull()
                .isExactlyInstanceOf(ArrayList.class)
                .hasSize(2);
    }
}
