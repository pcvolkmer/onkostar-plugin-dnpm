package DNPM.analyzer;

import DNPM.security.DelegatingDataBasedPermissionEvaluator;
import DNPM.security.PermissionType;
import DNPM.services.FormService;
import DNPM.services.mtb.MtbService;
import DNPM.services.therapieplan.MultipleMtbTherapieplanService;
import DNPM.services.therapieplan.TherapieplanService;
import DNPM.services.therapieplan.TherapieplanServiceFactory;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapieplanAnalyzerTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    @Mock
    private TherapieplanServiceFactory therapieplanServiceFactory;

    @Mock
    private TherapieplanService therapieplanService;

    @Mock
    private MtbService mtbService;

    @Mock
    private DelegatingDataBasedPermissionEvaluator permissionEvaluator;

    private TherapieplanAnalyzer therapieplanAnalyzer;

    @BeforeEach
    void setUp() {
        this.therapieplanAnalyzer = new TherapieplanAnalyzer(therapieplanServiceFactory, mtbService, permissionEvaluator);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        when(this.therapieplanServiceFactory.currentUsableInstance())
                .thenReturn(new MultipleMtbTherapieplanService(onkostarApi, formService));

        this.therapieplanAnalyzer.analyze(new Procedure(onkostarApi), null);

        verify(this.therapieplanServiceFactory, times(1)).currentUsableInstance();
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

        when(this.permissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class))).thenReturn(true);

        var input = Map.of("id", (Object) 1234);
        this.therapieplanAnalyzer.getProtokollauszug(input);

        var captor = ArgumentCaptor.forClass(List.class);
        verify(mtbService, times(1)).getProtocol(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void shouldNotRequestProtokollauszugDueToNoPermission() {
        when(this.permissionEvaluator.hasPermission(any(), anyInt(), anyString(), any(PermissionType.class)))
                .thenReturn(false);

        var input = Map.of("id", (Object) 1234);
        this.therapieplanAnalyzer.getProtokollauszug(input);

        verify(mtbService, times(0)).getProtocol(anyList());
    }

}
