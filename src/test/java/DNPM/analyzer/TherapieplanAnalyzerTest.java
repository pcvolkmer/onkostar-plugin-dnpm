package DNPM.analyzer;

import DNPM.services.FormService;
import DNPM.services.StudienService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.constants.JaNeinUnbekannt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    @Mock
    private StudienService studienService;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(onkostarApi, formService, studienService);
    }

    @Test
    void shouldNotUpdateSubformsOrSectionsIfMultipleMtbConfiguration() throws Exception {
        doAnswer(invocationOnMock -> {
            var settingName = invocationOnMock.getArgument(0, String.class);
            if (settingName.equals("mehrere_mtb_in_mtbepisode")) {
                return "true";
            }
            return null;
        }).when(onkostarApi).getGlobalSetting(anyString());

        this.therapieplanAnalyzer.analyze(new Procedure(onkostarApi), null);

        verify(onkostarApi, never()).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldNotUpdateSectionsIfSectionsNotEnabled() throws Exception {
        when(onkostarApi.getGlobalSetting(anyString())).thenReturn(null);

        var testProcedure = baseProcedure(onkostarApi);

        // Keine humangenetische Beratung und keine Reevaluation empfohlen
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.NEIN.getCode()));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.NEIN.getCode()));

        this.therapieplanAnalyzer.analyze(testProcedure, null);

        verify(onkostarApi, never()).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldUpdateSectionsIfNoReevaluation() throws Exception {
        when(onkostarApi.getGlobalSetting(anyString())).thenReturn(null);

        var testProcedure = baseProcedure(onkostarApi);

        // Humangenetische Beratung aber keine Reevaluation
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.JA.getCode()));
        testProcedure.setValue("humangenberbegruendung", new Item("humangen_ber_begruendung", "Das ist die Begründung"));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.NEIN.getCode()));

        this.therapieplanAnalyzer.analyze(testProcedure, null);

        var captor = ArgumentCaptor.forClass(Procedure.class);
        verify(onkostarApi, times(1)).saveProcedure(captor.capture(), anyBoolean());

        var capturedProcedure = captor.getValue();

        assertThat(capturedProcedure.getValue("reftkreevaluation")).isNull();
        assertThat(capturedProcedure.getValue("datumtkreevaluation")).isNull();

        assertThat(capturedProcedure.getValue("reftkhumangenber")).isNotNull();
        assertThat(capturedProcedure.getValue("reftkhumangenber").getInt()).isEqualTo(procedureId);
        assertThat(capturedProcedure.getValue("datumtkhumangenber")).isNotNull();
        assertThat(capturedProcedure.getValue("datumtkhumangenber").getDate()).isEqualTo(testDate);
    }

    @Test
    void shouldUpdateSectionsIfNoHumanGenConsultation() throws Exception {
        when(onkostarApi.getGlobalSetting(anyString())).thenReturn(null);

        var testProcedure = baseProcedure(onkostarApi);

        // Humangenetische Beratung aber keine Reevaluation
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.NEIN.getCode()));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.JA.getCode()));

        this.therapieplanAnalyzer.analyze(testProcedure, null);

        var captor = ArgumentCaptor.forClass(Procedure.class);
        verify(onkostarApi, times(1)).saveProcedure(captor.capture(), anyBoolean());

        var capturedProcedure = captor.getValue();

        assertThat(capturedProcedure.getValue("reftkhumangenber")).isNull();
        assertThat(capturedProcedure.getValue("datumtkhumangenber")).isNull();

        assertThat(capturedProcedure.getValue("reftkreevaluation")).isNotNull();
        assertThat(capturedProcedure.getValue("reftkreevaluation").getInt()).isEqualTo(procedureId);
        assertThat(capturedProcedure.getValue("datumtkreevaluation")).isNotNull();
        assertThat(capturedProcedure.getValue("datumtkreevaluation").getDate()).isEqualTo(testDate);
    }

    @Test
    void shouldRequestAllStudienForEmptyQueryString() {
        var input = Map.of("q", (Object)"   ");
        this.therapieplanAnalyzer.getStudien(input);

        verify(studienService, times(1)).findAll();
    }

    @Test
    void shouldRequestAllStudienForEmptyInputMap() {
        var input = new HashMap<String, Object>();
        this.therapieplanAnalyzer.getStudien(input);

        verify(studienService, times(1)).findAll();
    }

    @Test
    void shouldRequestFilteredStudien() {
        var input = Map.of("q", (Object)"NCT-123");
        this.therapieplanAnalyzer.getStudien(input);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(studienService, times(1)).findByQuery(captor.capture());
        assertThat(captor.getValue()).isEqualTo("NCT-123");
    }


    private static final int procedureId = 1234;
    private static final Date testDate = Date.from(Instant.parse("2023-03-15T09:43:00Z"));

    private Procedure baseProcedure(final IOnkostarApi onkostarApi) {
        var testProcedure = new Procedure(onkostarApi);
        testProcedure.setId(1000);

        // Setzen MTB Referenz und Datum MTB
        testProcedure.setValue("referstemtb", new Item("ref_tumorkonferenz", procedureId));
        testProcedure.setValue("datum", new Item("datum", testDate));

        return testProcedure;
    }

}
