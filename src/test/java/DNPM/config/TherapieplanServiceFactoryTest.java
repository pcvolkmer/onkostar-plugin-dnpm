package DNPM.config;

import DNPM.services.DefaultTherapieplanService;
import DNPM.services.FormService;
import DNPM.services.MultipleMtbTherapieplanService;
import DNPM.services.TherapieplanServiceFactory;
import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TherapieplanServiceFactoryTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    private TherapieplanServiceFactory therapieplanServiceFactory;

    @BeforeEach
    void setup() {
        this.therapieplanServiceFactory = new TherapieplanServiceFactory(onkostarApi, formService);
    }

    @Test
    void testShouldReturnDefaultTherapieplanServiceIfSettingIsFalse() {
        doAnswer(invocationOnMock -> {
            var settingName = invocationOnMock.getArgument(0, String.class);
            if (settingName.equals("mehrere_mtb_in_mtbepisode")) {
                return "false";
            }
            return null;
        }).when(onkostarApi).getGlobalSetting(anyString());

        var actual = this.therapieplanServiceFactory.currentUsableinstance();

        assertThat(actual).isInstanceOf(DefaultTherapieplanService.class);
    }

    @Test
    void testShouldReturnDefaultTherapieplanServiceIfNoSetting() {
        when(onkostarApi.getGlobalSetting(anyString())).thenReturn(null);

        var actual = this.therapieplanServiceFactory.currentUsableinstance();

        assertThat(actual).isInstanceOf(DefaultTherapieplanService.class);
    }

    @Test
    void testShouldReturnMultipleMtbTherapieplanServiceIfSettingIsTrue() {
        doAnswer(invocationOnMock -> {
            var settingName = invocationOnMock.getArgument(0, String.class);
            if (settingName.equals("mehrere_mtb_in_mtbepisode")) {
                return "true";
            }
            return null;
        }).when(onkostarApi).getGlobalSetting(anyString());

        var actual = this.therapieplanServiceFactory.currentUsableinstance();

        assertThat(actual).isInstanceOf(MultipleMtbTherapieplanService.class);
    }
}
