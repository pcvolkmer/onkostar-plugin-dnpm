package DNPM.services;

import de.itc.onkostar.api.IOnkostarApi;

public class TherapieplanServiceFactory {

    private final IOnkostarApi onkostarApi;

    private final FormService formService;

    public TherapieplanServiceFactory(IOnkostarApi onkostarApi, FormService formService) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
    }

    public TherapieplanService currentUsableInstance() {
        if (
                null != onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode")
                        && onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode").equals("true")
        ) {
            return new MultipleMtbTherapieplanService();
        }

        return new DefaultTherapieplanService(onkostarApi, formService);
    }

}
