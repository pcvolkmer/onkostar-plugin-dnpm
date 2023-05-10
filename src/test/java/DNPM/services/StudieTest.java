package DNPM.services;

import DNPM.dto.Studie;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudieTest {

    @Test
    void shouldDetectStudieWithNctNumber() {
        var studie = new Studie(
                "Kat 1",
                1,
                null,
                "Nct-12345678",
                "Teststudie 1",
                "Teststudie 1"
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.NCT);
    }

    @Test
    void shouldDetectStudieWithEudraCtNumber() {
        var studie = new Studie(
                "Kat 1",
                1,
                null,
                "2023-012345-12",
                "Teststudie 1",
                "Teststudie 1"
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.EUDRA_CT);
    }

    @Test
    void shouldReturnStudieWithUnknownNumberScheme() {
        var studie = new Studie(
                "Kat 1",
                1,
                "teststudie1",
                null,
                "Teststudie 1",
                "Teststudie 1"
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.UNKNOWN);
    }

}
