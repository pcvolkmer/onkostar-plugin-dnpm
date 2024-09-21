package dev.dnpm.config;

import dev.dnpm.services.FormService;
import dev.dnpm.services.SettingsService;
import dev.dnpm.services.therapieplan.TherapieplanServiceFactory;
import dev.dnpm.services.consent.ConsentManagerServiceFactory;
import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PluginConfigurationTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private SettingsService settingsService;

    @Mock
    private FormService formService;

    private PluginConfiguration configuration;

    @BeforeEach
    void setup() {
        this.configuration = new PluginConfiguration();
    }

    @Test
    void testShouldReturnConsentManagerServiceFactory() {
        var actual = this.configuration.consentManagerServiceFactory(onkostarApi);
        assertThat(actual).isInstanceOf(ConsentManagerServiceFactory.class);
    }

    @Test
    void testShouldReturnTherapieplanServiceFactory() {
        var actual = this.configuration.therapieplanServiceFactory(onkostarApi, settingsService, formService);
        assertThat(actual).isInstanceOf(TherapieplanServiceFactory.class);
    }
}
