package DNPM.forms;

import DNPM.services.FormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.constants.JaNeinUnbekannt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(onkostarApi, formService);
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

        var testProcedure = new Procedure(onkostarApi);
        testProcedure.setId(1000);

        // Setzen MTB Referenz und Datum MTB
        testProcedure.setValue("referstemtb", new Item("ref_tumorkonferenz", 1234));
        testProcedure.setValue("datum", new Item("datum", new Date()));

        // Keine humangenetische Beratung und keine Reevaluation empfohlen
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.NEIN));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.NEIN));

        this.therapieplanAnalyzer.analyze(testProcedure, null);

        verify(onkostarApi, never()).saveProcedure(any(Procedure.class), anyBoolean());
    }

}
