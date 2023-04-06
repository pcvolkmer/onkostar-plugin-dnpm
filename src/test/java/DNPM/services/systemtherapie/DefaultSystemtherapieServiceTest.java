package DNPM.services.systemtherapie;

import DNPM.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
