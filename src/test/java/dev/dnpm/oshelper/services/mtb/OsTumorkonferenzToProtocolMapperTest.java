package dev.dnpm.oshelper.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OsTumorkonferenzToProtocolMapperTest {

    private IOnkostarApi onkostarApi;

    private OsTumorkonferenzToProtocolMapper mapper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.mapper = new OsTumorkonferenzToProtocolMapper();
    }

    @Test
    void testShouldReturnMtbProtocolForDefaultImplementation() {
        var procedure = new Procedure(onkostarApi);
        procedure.setFormName("OS.Tumorkonferenz");
        procedure.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));

        var actual = mapper.apply(procedure);

        assertThat(actual)
                .isPresent()
                .contains("Fragestellung:\nTest ok?\n\nEmpfehlung:\nRerun Test if not ok!");
    }

}
