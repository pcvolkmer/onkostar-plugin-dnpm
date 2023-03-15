package DNPM.services;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultTherapieplanServiceTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    private TherapieplanService service;

    @BeforeEach
    void setUp() {
        this.service = new DefaultTherapieplanService(onkostarApi, formService);
    }

    @Test
    void shouldNotUpdateSubformsOrSectionsIfMultipleMtbConfiguration() throws Exception {
        this.service.updateRequiredMtbEntries(new Procedure(onkostarApi));
        verify(onkostarApi, never()).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldNotUpdateSectionsIfSectionsNotEnabled() throws Exception {
        var testProcedure = baseProcedure(onkostarApi);

        // Keine humangenetische Beratung und keine Reevaluation empfohlen
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.NEIN.getCode()));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.NEIN.getCode()));

        this.service.updateRequiredMtbEntries(testProcedure);

        verify(onkostarApi, never()).saveProcedure(any(Procedure.class), anyBoolean());
    }

    @Test
    void shouldUpdateSectionsIfNoReevaluation() throws Exception {
        var testProcedure = baseProcedure(onkostarApi);

        // Humangenetische Beratung aber keine Reevaluation
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.JA.getCode()));
        testProcedure.setValue("humangenberbegruendung", new Item("humangen_ber_begruendung", "Das ist die Begründung"));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.NEIN.getCode()));

        this.service.updateRequiredMtbEntries(testProcedure);

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
        var testProcedure = baseProcedure(onkostarApi);

        // Humangenetische Beratung aber keine Reevaluation
        testProcedure.setValue("humangenberatung", new Item("humangen_beratung", JaNeinUnbekannt.NEIN.getCode()));
        testProcedure.setValue("reevaluation", new Item("reevaluation", JaNeinUnbekannt.JA.getCode()));

        this.service.updateRequiredMtbEntries(testProcedure);

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
