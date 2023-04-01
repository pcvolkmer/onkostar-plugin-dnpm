package DNPM.services.systemtherapie;

import DNPM.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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

    private static Set<Map.Entry<String, String>> expectedFormnameMappings() {
        return Map.ofEntries(Map.entry("2011", "OS.Systemische Therapie.VarianteUKW"), Map.entry("20119", "OS.Systemische Therapie.VarianteUKW"), Map.entry("Defaultwert", "OS.Systemische Therapie")).entrySet();
    }

    @ParameterizedTest
    @MethodSource("expectedFormnameMappings")
    void testShouldMapSidToFormName(Map.Entry<String, String> expectedMapping) {
        var actual = service.selectFormNameBySID(expectedMapping.getKey());
        assertThat(actual).isEqualTo(expectedMapping.getValue());
    }

    @Test
    void testShouldReturnSystemischeTherapienFromDiagnose() {
        when(settingsService.getSID()).thenReturn(Optional.of("12345"));

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
