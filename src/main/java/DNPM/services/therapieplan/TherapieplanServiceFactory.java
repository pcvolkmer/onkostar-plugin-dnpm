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
import de.itc.onkostar.api.IOnkostarApi;

public class TherapieplanServiceFactory {

    private final IOnkostarApi onkostarApi;

    private final SettingsService settingsService;

    private final FormService formService;

    public TherapieplanServiceFactory(
            final IOnkostarApi onkostarApi,
            final SettingsService settingsService,
            final FormService formService
    ) {
        this.onkostarApi = onkostarApi;
        this.settingsService = settingsService;
        this.formService = formService;
    }

    public TherapieplanService currentUsableInstance() {
        if (settingsService.multipleMtbsInMtbEpisode()) {
            return new MultipleMtbTherapieplanService(onkostarApi, formService);
        }

        return new DefaultTherapieplanService(onkostarApi, formService);
    }

}
