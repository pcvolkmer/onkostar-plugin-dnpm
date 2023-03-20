package DNPM.services;

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
            return new MultipleMtbTherapieplanService();
        }

        return new DefaultTherapieplanService(onkostarApi, formService);
    }

}
