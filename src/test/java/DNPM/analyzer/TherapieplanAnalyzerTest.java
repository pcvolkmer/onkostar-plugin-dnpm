package DNPM.analyzer;

import DNPM.services.MultipleMtbTherapieplanService;
import DNPM.services.StudienService;
import DNPM.services.TherapieplanServiceFactory;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private StudienService studienService;

    @Mock
    private TherapieplanServiceFactory therapieplanServiceFactory;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(studienService, therapieplanServiceFactory);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        when(this.therapieplanServiceFactory.currentUsableinstance()).thenReturn(new MultipleMtbTherapieplanService());

        this.therapieplanAnalyzer.analyze(new Procedure(onkostarApi), null);

        verify(this.therapieplanServiceFactory, times(1)).currentUsableinstance();
    }

    @Test
    void shouldRequestAllStudienForEmptyQueryString() {
        var input = Map.of("q", (Object) "   ");
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
        var input = Map.of("q", (Object) "NCT-123");
        this.therapieplanAnalyzer.getStudien(input);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(studienService, times(1)).findByQuery(captor.capture());
        assertThat(captor.getValue()).isEqualTo("NCT-123");
    }

}
