package DNPM;

import DNPM.services.consent.ConsentManagerServiceFactory;
import DNPM.services.consent.MrConsentManagerService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsentManagerTest {

    private IOnkostarApi onkostarApi;

    private ConsentManagerServiceFactory consentManagerServiceFactory;

    private ConsentManager consentManager;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock ConsentManagerServiceFactory consentManagerServiceFactory
    ) {
        this.onkostarApi = onkostarApi;
        this.consentManagerServiceFactory = consentManagerServiceFactory;
        this.consentManager = new ConsentManager(onkostarApi, consentManagerServiceFactory);
    }

    @Test
    void shouldRunServiceMethodsOnAnalyzeCalled() {
        var consentManagerServiceMock = mock(MrConsentManagerService.class);

        when(this.consentManagerServiceFactory.currentUsableInstance())
                .thenReturn(consentManagerServiceMock);

        this.consentManager.analyze(new Procedure(onkostarApi), null);

        verify(consentManagerServiceMock, times(1)).applyConsent(any(Procedure.class));
    }

}
