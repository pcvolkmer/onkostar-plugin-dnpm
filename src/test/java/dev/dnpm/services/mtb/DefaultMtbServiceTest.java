package dev.dnpm.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultMtbServiceTest {

    private IOnkostarApi onkostarApi;

    private DefaultMtbService service;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.service = new DefaultMtbService(onkostarApi);
    }

    private static Set<Map.Entry<String, Class<? extends ProcedureToProtocolMapper>>> expectedMappings() {
        return Map.ofEntries(
                Map.entry("OS.Tumorkonferenz", OsTumorkonferenzToProtocolMapper.class),
                Map.entry("OS.Tumorkonferenz.VarianteUKW", OsTumorkonferenzVarianteUkwToProtocolMapper.class),
                Map.entry("MR.MTB_Anmeldung", MrMtbAnmeldungToProtocolMapper.class)
        ).entrySet();
    }

    @ParameterizedTest
    @MethodSource("expectedMappings")
    void testShouldMapFormNameToMapper(Map.Entry<String, Class<?>> expectedMapping) {
        var procedure = new Procedure(onkostarApi);
        procedure.setFormName(expectedMapping.getKey());

        var actual = service.procedureToProtocolMapper(procedure);

        assertThat(actual).isExactlyInstanceOf(expectedMapping.getValue());
    }

    @Test
    void testShouldReturnMtbProtocolForDefaultImplementation() {
        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz");
        procedure1.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));

        var procedures = List.of(
                procedure1
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEqualTo("Fragestellung:\nTest ok?\n\nEmpfehlung:\nRerun Test if not ok!");
    }

    @Test
    void testShouldReturnMtbProtocolForMultipleTK() {
        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz");
        procedure1.setStartDate(Date.from(Instant.parse("2023-02-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test immer noch ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Do not rerun Test if ok!"));

        var procedure2 = new Procedure(onkostarApi);
        procedure2.setFormName("OS.Tumorkonferenz");
        procedure2.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure2.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure2.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));

        var procedures = List.of(
                procedure1,
                procedure2
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEqualTo(
                "Fragestellung:\nTest ok?\n\nEmpfehlung:\nRerun Test if not ok!\n\n" +
                        "Fragestellung:\nTest immer noch ok?\n\nEmpfehlung:\nDo not rerun Test if ok!"
        );
    }

    @Test
    void testShouldReturnMtbProtocolForMultipleTKVarianteUKW() {
        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz.VarianteUKW");
        procedure1.setStartDate(Date.from(Instant.parse("2023-02-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test immer noch ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Do not rerun Test if ok!"));

        var procedure2 = new Procedure(onkostarApi);
        procedure2.setFormName("OS.Tumorkonferenz.VarianteUKW");
        procedure2.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure2.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure2.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));


        var procedures = Arrays.asList(
                procedure1,
                procedure2
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEqualTo(
                "Fragestellung:\nTest ok?\n\nEmpfehlung:\nRerun Test if not ok!\n\n" +
                        "Fragestellung:\nTest immer noch ok?\n\nEmpfehlung:\nDo not rerun Test if ok!"
        );
    }

    @Test
    void testShouldReturnDistinctProtocolEntries() {
        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz.VarianteUKW");
        procedure1.setStartDate(Date.from(Instant.parse("2023-02-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test immer noch ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Do not rerun Test if ok!"));

        var procedure2 = new Procedure(onkostarApi);
        procedure2.setFormName("OS.Tumorkonferenz.VarianteUKW");
        procedure2.setStartDate(Date.from(Instant.parse("2023-02-01T00:00:00Z")));
        procedure2.setValue("Fragestellung", new Item("Fragestellung", "Test immer noch ok?"));
        procedure2.setValue("Empfehlung", new Item("Empfehlung", "Do not rerun Test if ok!"));

        var procedure3 = new Procedure(onkostarApi);
        procedure3.setFormName("OS.Tumorkonferenz.VarianteUKW");
        procedure3.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure3.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure3.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));


        var procedures = Arrays.asList(
                procedure1,
                procedure2,
                procedure3
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEqualTo(
                "Fragestellung:\nTest ok?\n\nEmpfehlung:\nRerun Test if not ok!\n\n" +
                        "Fragestellung:\nTest immer noch ok?\n\nEmpfehlung:\nDo not rerun Test if ok!"
        );
    }

    @Test
    void testShouldReturnEmptyMtbProtocolForUnknownForm() {
        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz.Unbekannt");
        procedure1.setStartDate(Date.from(Instant.parse("2023-02-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test immer noch ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Do not rerun Test if ok!"));

        var procedure2 = new Procedure(onkostarApi);
        procedure2.setFormName("OS.Tumorkonferenz.Unbekannt");
        procedure2.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure2.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure2.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));


        var procedures = Arrays.asList(
                procedure1,
                procedure2
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEmpty();
    }

}
