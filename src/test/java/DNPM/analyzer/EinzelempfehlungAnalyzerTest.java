package DNPM.analyzer;

import DNPM.security.DelegatingDataBasedPermissionEvaluator;
import DNPM.services.molekulargenetik.MolekulargenetikFormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EinzelempfehlungAnalyzerTest {

    private IOnkostarApi onkostarApi;

    private MolekulargenetikFormService molekulargenetikFormService;

    private EinzelempfehlungAnalyzer analyzer;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock MolekulargenetikFormService molekulargenetikFormService,
            @Mock DelegatingDataBasedPermissionEvaluator permissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.molekulargenetikFormService = molekulargenetikFormService;
        this.analyzer = new EinzelempfehlungAnalyzer(onkostarApi, molekulargenetikFormService, permissionEvaluator);
    }

    @Test
    void testShouldRequestVariantsFromMolekulargenetikFormService() {
        doAnswer(invocationOnMock -> new Procedure(this.onkostarApi)).when(onkostarApi).getProcedure(anyInt());

        analyzer.getVariants(Map.of("id", 123));
        verify(molekulargenetikFormService, times(1)).getVariants(any(Procedure.class));
    }

}
