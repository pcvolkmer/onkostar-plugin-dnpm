package DNPM.analyzer;

import DNPM.security.PermissionType;
import DNPM.security.PersonPoolBasedPermissionEvaluator;
import DNPM.services.StudienService;
import DNPM.services.molekulargenetik.MolekulargenetikFormService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EinzelempfehlungAnalyzerTest {

    private IOnkostarApi onkostarApi;

    private StudienService studienService;

    private MolekulargenetikFormService molekulargenetikFormService;

    private PersonPoolBasedPermissionEvaluator permissionEvaluator;

    private EinzelempfehlungAnalyzer analyzer;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock StudienService studienService,
            @Mock MolekulargenetikFormService molekulargenetikFormService,
            @Mock PersonPoolBasedPermissionEvaluator permissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.studienService = studienService;
        this.molekulargenetikFormService = molekulargenetikFormService;
        this.permissionEvaluator = permissionEvaluator;
        this.analyzer = new EinzelempfehlungAnalyzer(onkostarApi, studienService, molekulargenetikFormService, permissionEvaluator);
    }

    @Test
    void testShouldRequestVariantsFromMolekulargenetikFormService() {
        doAnswer(invocationOnMock -> new Procedure(this.onkostarApi)).when(onkostarApi).getProcedure(anyInt());
        when(this.permissionEvaluator.hasPermission(any(), any(Procedure.class), any(PermissionType.class)))
                .thenReturn(true);

        analyzer.getVariants(Map.of("id", 123));
        verify(molekulargenetikFormService, times(1)).getVariants(any(Procedure.class));
    }

    @Test
    void shouldRequestAllStudienForEmptyQueryString() {
        var input = Map.of("q", (Object) "   ");
        this.analyzer.getStudien(input);

        verify(studienService, times(1)).findActive();
    }

    @Test
    void shouldRequestActiveStudienForEmptyInputMap() {
        var input = new HashMap<String, Object>();
        this.analyzer.getStudien(input);

        verify(studienService, times(1)).findActive();
    }

    @Test
    void shouldRequestFilteredActiveStudien() {
        var input = Map.of("q", (Object) "NCT-123");
        this.analyzer.getStudien(input);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(studienService, times(1)).findActiveByQuery(captor.capture());
        assertThat(captor.getValue()).isEqualTo("NCT-123");
    }

    @Test
    void shouldRequestActiveStudien() {
        var input = Map.of("q", (Object) "");
        this.analyzer.getStudien(input);

        verify(studienService, times(1)).findActive();
    }

    @Test
    void shouldRequestAllFilteredtudien() {
        var input = Map.of("q", (Object) "NCT-123");
        this.analyzer.getStudien(input);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(studienService, times(1)).findActiveByQuery(captor.capture());
        assertThat(captor.getValue()).isEqualTo("NCT-123");
    }

}
