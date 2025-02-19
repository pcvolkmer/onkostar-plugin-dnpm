package dev.dnpm.services.therapieplan;

import de.itc.onkostar.api.IOnkostarApi;
import dev.dnpm.services.FormService;
import dev.dnpm.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
    void testShouldReturnDefaultTherapieplanServiceIfSettingIsFalse() {
        when(settingsService.multipleMtbsInMtbEpisode()).thenReturn(false);

        var actual = this.therapieplanServiceFactory.currentUsableInstance();

        assertThat(actual).isInstanceOf(DefaultTherapieplanService.class);
    }

    @Test
    void testShouldReturnMultipleMtbTherapieplanServiceIfSettingIsTrue() {
        when(settingsService.multipleMtbsInMtbEpisode()).thenReturn(true);

        var actual = this.therapieplanServiceFactory.currentUsableInstance();

        assertThat(actual).isInstanceOf(MultipleMtbTherapieplanService.class);
    }
}
