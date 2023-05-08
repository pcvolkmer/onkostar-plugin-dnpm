/*
 * MIT License
 *
 * 2023 Comprehensive Cancer Center Mainfranken
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package DNPM.services.therapieplan;

import DNPM.services.FormService;
import DNPM.services.SettingsService;
import DNPM.services.therapieplan.DefaultTherapieplanService;
import DNPM.services.therapieplan.MultipleMtbTherapieplanService;
import DNPM.services.therapieplan.TherapieplanServiceFactory;
import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TherapieplanServiceFactoryTest {

    @Mock
    private IOnkostarApi onkostarApi;

    @Mock
    private FormService formService;

    @Mock
    private SettingsService settingsService;

    private TherapieplanServiceFactory therapieplanServiceFactory;

    @BeforeEach
    void setup() {
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
