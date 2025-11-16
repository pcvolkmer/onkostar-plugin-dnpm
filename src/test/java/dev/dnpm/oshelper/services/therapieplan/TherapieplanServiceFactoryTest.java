package dev.dnpm.oshelper.services.therapieplan;

import de.itc.onkostar.api.IOnkostarApi;
import dev.dnpm.oshelper.services.FormService;
import dev.dnpm.oshelper.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TherapieplanServiceFactoryTest {

    private SettingsService settingsService;

    private TherapieplanServiceFactory therapieplanServiceFactory;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SettingsService settingsService,
            @Mock FormService formService
    ) {
        this.settingsService = settingsService;
        this.therapieplanServiceFactory = new TherapieplanServiceFactory(onkostarApi, settingsService, formService);
    }

    @Test
    void testShouldAlwaysReturnDefaultTherapieplanService() {
        var actual = this.therapieplanServiceFactory.currentUsableInstance();
        assertThat(actual).isInstanceOf(DefaultTherapieplanService.class);
    }
}
