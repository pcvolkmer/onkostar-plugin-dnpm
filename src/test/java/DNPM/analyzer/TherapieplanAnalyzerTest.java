package DNPM.analyzer;

import DNPM.services.*;
import DNPM.services.mtb.MtbService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    @Mock
    private StudienService studienService;

    @Mock
    private TherapieplanServiceFactory therapieplanServiceFactory;

    @Mock
    private TherapieplanService therapieplanService;

    @Mock
    private MtbService mtbService;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(studienService, therapieplanServiceFactory, mtbService);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        when(this.therapieplanServiceFactory.currentUsableInstance())
                .thenReturn(new MultipleMtbTherapieplanService(onkostarApi, formService));

        this.therapieplanAnalyzer.analyze(new Procedure(onkostarApi), null);

        verify(this.therapieplanServiceFactory, times(1)).currentUsableInstance();
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

    @Test
    void shouldRequestProtokollauszug() {
        doAnswer(invocationOnMock -> {
            var procedure = new Procedure(onkostarApi);
            procedure.setValue("referstemtb", new Item("referstemtb", 2345));
            return List.of(procedure);
        }).when(this.therapieplanService).findReferencedMtbs(anyInt());

        when(this.therapieplanServiceFactory.currentUsableInstance())
                .thenReturn(therapieplanService);

        var input = Map.of("id", (Object) 1234);
        this.therapieplanAnalyzer.getProtokollauszug(input);

        var captor = ArgumentCaptor.forClass(List.class);
        verify(mtbService, times(1)).getProtocol(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

}
