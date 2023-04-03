package DNPM;

import DNPM.services.consent.ConsentManagerServiceFactory;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsentManager implements IProcedureAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    private final ConsentManagerServiceFactory consentManagerServiceFactory;

    public ConsentManager(
            final IOnkostarApi onkostarApi,
            final ConsentManagerServiceFactory consentManagerServiceFactory
    ) {
        this.onkostarApi = onkostarApi;
        this.consentManagerServiceFactory = consentManagerServiceFactory;
    }

    @Override
    public String getDescription() {
        return "Aktualisiert Consent Daten in verknüpften Formularen";
    }

    @Override
    public String getName() {
        return "Consent Manager";
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure prozedur, Disease erkrankung) {
        return prozedur.getFormName().equals(onkostarApi.getGlobalSetting("consentform"));
    }

    @Override
    public boolean isRelevantForDeletedProcedure() {
        // TODO is relevant for deleted procedure = true
        return false;
    }

    @Override
    public boolean isSynchronous() {
        return true;
    }

    @Override
    public void analyze(Procedure prozedur, Disease erkrankung) {
        consentManagerServiceFactory.currentUsableInstance().applyConsent(prozedur);
    }

}
