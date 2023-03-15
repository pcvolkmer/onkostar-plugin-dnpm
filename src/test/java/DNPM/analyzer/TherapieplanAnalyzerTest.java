package DNPM.analyzer;

import DNPM.services.StudienService;
import DNPM.services.TherapieplanService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private StudienService studienService;

    @Mock
    private TherapieplanService therapieplanService;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(studienService, therapieplanService);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        this.therapieplanAnalyzer.analyze(new Procedure(onkostarApi), null);

        verify(this.therapieplanService, times(1)).updateRequiredMtbEntries(any(Procedure.class));
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

}
