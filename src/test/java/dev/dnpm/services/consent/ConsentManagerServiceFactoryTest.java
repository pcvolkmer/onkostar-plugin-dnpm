package dev.dnpm.services.consent;

import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentManagerServiceFactoryTest {

    private IOnkostarApi onkostarApi;

    private ConsentManagerServiceFactory consentManagerServiceFactory;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.consentManagerServiceFactory = new ConsentManagerServiceFactory(onkostarApi);
    }

    private static Set<Map.Entry<String, Class<? extends ConsentManagerService>>> expectedMappings() {
        return Map.ofEntries(
                Map.entry("MR.Consent", MrConsentManagerService.class),
                Map.entry("Excel-Formular", UkwConsentManagerService.class)
        ).entrySet();
    }

    @ParameterizedTest
    @MethodSource("expectedMappings")
    void testShouldMapFormNameToService(Map.Entry<String, Class<?>> expectedMapping) {
        when(onkostarApi.getGlobalSetting(anyString())).thenReturn(expectedMapping.getKey());

        var actual = consentManagerServiceFactory.currentUsableInstance();

        assertThat(actual).isExactlyInstanceOf(expectedMapping.getValue());
    }

}
