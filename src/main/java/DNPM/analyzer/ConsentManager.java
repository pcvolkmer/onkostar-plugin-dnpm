package DNPM.analyzer;

import DNPM.services.consent.ConsentManagerServiceFactory;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsentManager extends Analyzer {

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
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
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
        var consentManagerService = consentManagerServiceFactory.currentUsableInstance();
        if (! consentManagerService.canApply(prozedur)) {
            logger.error("Fehler im ConsentManagement: Kann Prozedur mit Formularnamen '{}' nicht anwenden", prozedur.getFormName());
            return;
        }
        consentManagerService.applyConsent(prozedur);
    }

}
