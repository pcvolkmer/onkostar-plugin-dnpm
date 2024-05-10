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
