package DNPM.services.consent;

import de.itc.onkostar.api.IOnkostarApi;

public class ConsentManagerServiceFactory {

    private final IOnkostarApi onkostarApi;

    public ConsentManagerServiceFactory(
            final IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
    }

    public ConsentManagerService currentUsableInstance() {
        var consentFormName = onkostarApi.getGlobalSetting("consentform");

        switch (consentFormName) {
            case "Excel-Formular":
                return new UkwConsentManagerService(this.onkostarApi);
            case "MR.Consent":
            default:
                return new MrConsentManagerService(this.onkostarApi);
        }
    }

}
