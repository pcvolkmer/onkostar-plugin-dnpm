package DNPM;

import DNPM.services.systemtherapie.SystemtherapieService;
import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DNPMHelperTest {

    private SystemtherapieService systemtherapieService;

    private DNPMHelper dnpmHelper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SystemtherapieService systemtherapieService
    ) {
        this.systemtherapieService = systemtherapieService;
        this.dnpmHelper = new DNPMHelper(onkostarApi, systemtherapieService);
    }

    @Test
    void testShouldRequestSystemischeTherapienFromDiagnose() {
        dnpmHelper.getSystemischeTherapienFromDiagnose(Map.of("DiagnoseId", 1234));

        var captor = ArgumentCaptor.forClass(Integer.class);
        verify(systemtherapieService, times(1)).getSystemischeTherapienFromDiagnose(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1234);
    }

    @Test
    void testShouldReturnNullIfNoDiagnoseId() {
        var actual = dnpmHelper.getSystemischeTherapienFromDiagnose(new HashMap<>());

        assertThat(actual).isNull();
    }

}
