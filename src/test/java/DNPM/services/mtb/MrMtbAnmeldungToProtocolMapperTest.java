package DNPM.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class MrMtbAnmeldungToProtocolMapperTest {

    private IOnkostarApi onkostarApi;

    private MrMtbAnmeldungToProtocolMapper mapper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.mapper = new MrMtbAnmeldungToProtocolMapper(onkostarApi);
    }

    @Test
    void testShouldMapCompletedForm() {
        var anmeldung = new Procedure(onkostarApi);
        anmeldung.setId(1);
        anmeldung.setFormName("MR.MTB_Anmeldung");
        anmeldung.setValue("Fragestellung", new Item("Fragestellung", "Frage?"));
        anmeldung.setValue("Empfehlung", new Item("Empfehlung", 2));

        var empfehlung = new Procedure(onkostarApi);
        empfehlung.setId(2);
        empfehlung.setFormName("MR.MTB_Empfehlung");

        var einzelempfehlung1 = new Procedure(onkostarApi);
        einzelempfehlung1.setId(10);
        einzelempfehlung1.setFormName("MR.MTB_Einzelempfehlung");
        einzelempfehlung1.setValue("Prioritaet", new Item("Empfehlungsprio", 1));
        einzelempfehlung1.setValue("Empfehlung", new Item("Empfehlung", "Empfehlung1"));

        var einzelempfehlung2 = new Procedure(onkostarApi);
        einzelempfehlung2.setId(20);
        einzelempfehlung2.setFormName("MR.MTB_Einzelempfehlung");
        einzelempfehlung2.setValue("Prioritaet", new Item("Empfehlungsprio", 2));
        einzelempfehlung2.setValue("Empfehlung", new Item("Empfehlung", "Empfehlung2"));

        doAnswer(invocationOnMock -> {
            var procedureId = invocationOnMock.getArgument(0, Integer.class);
            if (2 == procedureId) {
                return empfehlung;
            } else if (10 == procedureId) {
                return einzelempfehlung1;
            } else if (20 == procedureId) {
                return einzelempfehlung2;
            }
            return null;
        }).when(onkostarApi).getProcedure(anyInt());

        doAnswer(invocationOnMock -> {
            var procedureId = invocationOnMock.getArgument(0, Integer.class);
            if (2 == procedureId) {
                return Set.of(einzelempfehlung1, einzelempfehlung2);
            }
            return null;
        }).when(onkostarApi).getSubprocedures(anyInt());

        var actual = this.mapper.apply(anmeldung);

        assertThat(actual)
                .isPresent()
                .contains(
                        "Fragestellung:\nFrage?\n\n"
                                + "Empfehlung:\nEmpfehlung1\n\n"
                                + "Empfehlung:\nEmpfehlung2"
                );
    }

    @Test
    void testShouldMapFormWithMissingEinzelempfehlungen() {
        var anmeldung = new Procedure(onkostarApi);
        anmeldung.setId(1);
        anmeldung.setFormName("MR.MTB_Anmeldung");
        anmeldung.setValue("Fragestellung", new Item("Fragestellung", "Frage?"));
        anmeldung.setValue("Empfehlung", new Item("Empfehlung", 2));

        var empfehlung = new Procedure(onkostarApi);
        empfehlung.setId(2);
        empfehlung.setFormName("MR.MTB_Empfehlung");

        doAnswer(invocationOnMock -> {
            var procedureId = invocationOnMock.getArgument(0, Integer.class);
            if (2 == procedureId) {
                return empfehlung;
            }
            return null;
        }).when(onkostarApi).getProcedure(anyInt());

        var actual = this.mapper.apply(anmeldung);

        assertThat(actual)
                .isPresent()
                .contains("Fragestellung:\nFrage?");
    }

    @Test
    void testShouldMapFormWithMissingEmpfehlung() {
        var anmeldung = new Procedure(onkostarApi);
        anmeldung.setId(1);
        anmeldung.setFormName("MR.MTB_Anmeldung");
        anmeldung.setValue("Fragestellung", new Item("Fragestellung", "Frage?"));

        var actual = this.mapper.apply(anmeldung);

        assertThat(actual)
                .isPresent()
                .contains("Fragestellung:\nFrage?");
    }

    @Test
    void testShouldMapFormWithMissingFragestellungAndEmpfehlung() {
        var anmeldung = new Procedure(onkostarApi);
        anmeldung.setId(1);
        anmeldung.setFormName("MR.MTB_Anmeldung");

        var actual = this.mapper.apply(anmeldung);

        assertThat(actual).isEmpty();
    }
}
