package DNPM;

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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class DNPMHelperTest {

    private IOnkostarApi onkostarApi;

    private DNPMHelper dnpmHelper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.dnpmHelper = new DNPMHelper(onkostarApi);
    }

    @Test
    void testShouldReturnSystemischeTherapienFromDiagnose() {
        doAnswer(invocationOnMock -> {
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

            return List.of(procedure);
        }).when(this.onkostarApi).getProceduresForDiseaseByForm(anyInt(), anyString());

        var actual = dnpmHelper.getSystemischeTherapienFromDiagnose(Map.of("DiagnoseId", 1));

        assertThat(actual).isNotNull();
        assertThat(actual).isExactlyInstanceOf(ArrayList.class);

        @SuppressWarnings("unchecked")
        var actualList = (ArrayList<Map<String, Object>>) actual;
        assertThat(actualList).hasSize(1);

        assertThat(actualList.get(0).get("Beginn"))
                .isEqualTo(Date.from(Instant.parse("2023-01-01T00:00:00Z")).toString());
        assertThat(actualList.get(0).get("Ende"))
                .isEqualTo(Date.from(Instant.parse("2023-01-31T00:00:00Z")).toString());
        assertThat(actualList.get(0).get("Beendigung"))
                .isEqualTo("E");
        assertThat(actualList.get(0).get("Ergebnis"))
                .isEqualTo("T");
        assertThat(actualList.get(0).get("Wirkstoffe"))
                .isEqualTo("Testsubstanz, cyclophosphamide");
        assertThat(actualList.get(0).get("WirkstoffCodes"))
                .isEqualTo(
                        "[" +
                                "{\"system\":\"other\",\"code\":\"Testsubstanz\",\"substance\":\"Testsubstanz\"}," +
                                "{\"system\":\"ATC\",\"code\":\"L01AA01\",\"substance\":\"cyclophosphamide\"}" +
                                "]"
                );
    }

}
