package DNPM.config;

import DNPM.services.FormService;
import DNPM.services.SettingsService;
import DNPM.services.TherapieplanServiceFactory;
import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PluginConfigurationTest {

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
    void testShouldReturnTherapieplanServiceFactory() {
        var actual = this.configuration.therapieplanServiceFactory(onkostarApi, settingsService, formService);
        assertThat(actual).isInstanceOf(TherapieplanServiceFactory.class);
    }
}
