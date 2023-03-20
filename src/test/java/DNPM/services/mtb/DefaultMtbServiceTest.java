package DNPM.services.mtb;

import DNPM.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultMtbServiceTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private SettingsService settingsService;

    private DefaultMtbService service;

    @BeforeEach
    void setup() {
        this.service = new DefaultMtbService(settingsService);
    }

    @Test
    void testShouldReturnMtbProtocolForDefaultImplementation() {

        when(settingsService.getSID()).thenReturn(Optional.of("12345"));
        when(settingsService.multipleMtbsInMtbEpisode()).thenReturn(false);

        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz");
        procedure1.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));

        var procedures = List.of(
                procedure1
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEqualTo("Test ok?\nRerun Test if not ok!");
    }

    @Test
    void testShouldReturnEmptyMtbProtocolForMultipleMtb() {

        when(settingsService.getSID()).thenReturn(Optional.of("12345"));
        when(settingsService.multipleMtbsInMtbEpisode()).thenReturn(true);

        var procedure1 = new Procedure(onkostarApi);
        procedure1.setFormName("OS.Tumorkonferenz");
        procedure1.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure1.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure1.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));

        var procedures = List.of(
                procedure1
        );

        var actual = service.getProtocol(procedures);

        assertThat(actual).isEmpty();
    }

    @Test
    void testShouldReturnMtbProtocolForSID20119() {
        when(settingsService.getSID()).thenReturn(Optional.of("20119"));
        when(settingsService.multipleMtbsInMtbEpisode()).thenReturn(true);

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
                "Test ok?\nRerun Test if not ok!\nTest immer noch ok?\nDo not rerun Test if ok!"
        );
    }

}
