package DNPM.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MtbServiceTest {

    private IOnkostarApi onkostarApi;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
    }

    private static Set<Map.Entry<String, Class<? extends ProcedureToProtocolMapper>>> expectedMappings() {
        return Map.ofEntries(
                Map.entry("OS.Tumorkonferenz", OsTumorkonferenzToProtocolMapper.class),
                Map.entry("OS.Tumorkonferenz.VarianteUKW", OsTumorkonferenzVarianteUkwToProtocolMapper.class)
        ).entrySet();
    }

    @ParameterizedTest
    @MethodSource("expectedMappings")
    void testShouldMapFormNameToMapper(Map.Entry<String, Class<?>> expectedMapping) {
        var procedure = new Procedure(onkostarApi);
        procedure.setFormName(expectedMapping.getKey());

        var actual = MtbService.procedureToProtocolMapper(procedure);

        assertThat(actual).isExactlyInstanceOf(expectedMapping.getValue());
    }

}
