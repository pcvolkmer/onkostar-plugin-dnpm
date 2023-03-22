package DNPM.services.systemtherapie;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProzedurToProzedurwerteMapperTest {

    private IOnkostarApi onkostarApi;

    private OsSystemischeTherapieToProzedurwerteMapper mapper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.mapper = new OsSystemischeTherapieToProzedurwerteMapper();
    }

    @Test
    void testShouldReturnSystemischeTherapienFromDiagnose() {
        var procedure = new Procedure(onkostarApi);
        procedure.setFormName("OS.Systemische Therapie");
        procedure.setValue("Beginn", new Item("Beginn", Date.from(Instant.parse("2023-01-01T00:00:00Z"))));
        procedure.setValue("Ende", new Item("Ende", Date.from(Instant.parse("2023-01-31T00:00:00Z"))));
        procedure.setValue("Beendigung", new Item("Beendigungsstatus", "E"));
        procedure.setValue("Ergebnis", new Item("Ergebnis", "T"));

        var substanzen = new ArrayList<>();
        substanzen.add(Map.of(
                "Substanz", "Testsubstanz",
                "Substanz_shortDescription", "Testsubstanz"
        ));
        substanzen.add(Map.of(
                "Substanz", "L01AA01",
                "Substanz_shortDescription", "cyclophosphamide"
        ));
        procedure.setValue("SubstanzenList", new Item("SubstanzenList", substanzen));

        var actual = mapper.apply(procedure);

        assertThat(actual).isPresent();

        assertThat(actual.get().get("Beginn"))
                .isEqualTo(Date.from(Instant.parse("2023-01-01T00:00:00Z")).toString());
        assertThat(actual.get().get("Ende"))
                .isEqualTo(Date.from(Instant.parse("2023-01-31T00:00:00Z")).toString());
        assertThat(actual.get().get("Beendigung"))
                .isEqualTo("E");
        assertThat(actual.get().get("Ergebnis"))
                .isEqualTo("T");
        assertThat(actual.get().get("Wirkstoffe"))
                .isEqualTo("Testsubstanz, cyclophosphamide");
        assertThat(actual.get().get("WirkstoffCodes"))
                .isEqualTo(
                        "[" +
                                "{\"system\":\"other\",\"code\":\"Testsubstanz\",\"substance\":\"Testsubstanz\"}," +
                                "{\"system\":\"ATC\",\"code\":\"L01AA01\",\"substance\":\"cyclophosphamide\"}" +
                                "]"
                );
    }

}
