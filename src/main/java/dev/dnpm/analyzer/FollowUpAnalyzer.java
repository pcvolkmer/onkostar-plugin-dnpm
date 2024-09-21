package dev.dnpm.analyzer;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Diese Klasse implementiert ein Plugin, welches Aktionen nach Bearbeitung eines FollowUps durchführt.
 *
 * @since 0.0.2
 */
@Component
public class FollowUpAnalyzer extends Analyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    public FollowUpAnalyzer(IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    @Override
    public String getDescription() {
        return "Aktualisiert verknüpfte Formulare nach Änderungen im FollowUp-Formular";
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return null != procedure && procedure.getFormName().equals("DNPM FollowUp");
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    @Override
    public Set<AnalyseTriggerEvent> getTriggerEvents() {
        return Set.of(
                AnalyseTriggerEvent.EDIT_SAVE,
                AnalyseTriggerEvent.EDIT_LOCK,
                AnalyseTriggerEvent.REORG
        );
    }

    @Override
    public void analyze(Procedure procedure, Disease disease) {
        backlinkToEinzelempfehlung(procedure);
    }

    /**
     * Verlinke aktuelles FollowUp in angegebener Einzelempfehlung
     *
     * @param procedure Das FollowUp
     */
    private void backlinkToEinzelempfehlung(Procedure procedure) {
        if (null == procedure.getValue("LinkTherapieempfehlung")) {
            return;
        }

        var referencedProcedureId = procedure.getValue("LinkTherapieempfehlung");
        if (null == referencedProcedureId || referencedProcedureId.getInt() == 0) {
            // Alles gut, es ist keine Einzelempfehlung angegeben
            return;
        }

        var referencedProcedure = onkostarApi.getProcedure(referencedProcedureId.getInt());
        if (null == referencedProcedure) {
            logger.error("Referenzierte Einzelempfehlung wurde nicht gefunden: {}", referencedProcedureId);
            return;
        }

        referencedProcedure.setValue("refdnpmfollowup", new Item("ref_dnpm_followup", procedure.getId()));

        try {
            onkostarApi.saveProcedure(referencedProcedure);
        } catch (Exception e) {
            logger.error("FollowUp konnte nicht mit Einzelempfehlung verknüpft werden", e);
        }
    }
}
