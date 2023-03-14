package DNPM.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StudieTest {

    @Test
    void shouldDetectStudieWithNctNumber() {
        var studie = new Studie(
                "teststudie1",
                "Nct-12345678",
                "Teststudie 1",
                "Teststudie 1",
                1
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.NCT);
    }

    @Test
    void shouldDetectStudieWithEudraCtNumber() {
        var studie = new Studie(
                "teststudie1",
                "2023-012345-12",
                "Teststudie 1",
                "Teststudie 1",
                1
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.EUDRA_CT);
    }

    @Test
    void shouldReturnStudieWithUnknownNumberScheme() {
        var studie = new Studie(
                "teststudie1",
                null,
                "Teststudie 1",
                "Teststudie 1",
                1
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.UNKNOWN);
    }

}
