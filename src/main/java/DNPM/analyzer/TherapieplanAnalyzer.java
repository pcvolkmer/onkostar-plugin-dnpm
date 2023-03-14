package DNPM.analyzer;

import DNPM.services.FormService;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

/**
 * Diese Klasse implementiert ein Plugin, welches Aktionen nach Bearbeitung eines Therapieplans durchführt.
 *
 * @since 0.0.2
 */
@Component
public class TherapieplanAnalyzer implements IProcedureAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    private final FormService formService;

    public TherapieplanAnalyzer(final IOnkostarApi onkostarApi, final FormService formService) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
    }

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

    @Override
    public String getVersion() {
        return "0.1.0";
    }

    @Override
    public String getName() {
        return "DNPM Therapieplan Analyzer";
    }

    @Override
    public String getDescription() {
        return "Aktualisiert Unterformulare nach Änderungen im Therapieplan-Formular";
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
        return null != procedure && procedure.getFormName().equals("DNPM Therapieplan");
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
        updateMtbInSections(procedure);
        updateMtbInSubforms(procedure);
    }

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in weiteren Bereichen
     * "Humangenetische Beratung" und "Reevaluation", wenn erforderlich.
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    private void updateMtbInSections(Procedure procedure) {
        if (
                null != onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode")
                        && onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode").equals("true")
                        ||
                        !procedure.getValue("humangenberatung").getString().equals("1")
                                && !procedure.getValue("reevaluation").getString().equals("1")
        ) {
            return;
        }

        var mtbReference = procedure.getValue("referstemtb").getInt();
        var mtbDate = procedure.getValue("datum").getDate();
        var noUpdateRequired = true;

        if (mtbReference != procedure.getValue("reftkhumangenber").getInt() && !mtbDate.equals(procedure.getValue("datumtkhumangenber").getDate())) {
            procedure.setValue("reftkhumangenber", new Item("ref_tk_humangenber", mtbReference));
            procedure.setValue("datumtkhumangenber", new Item("datum_tk_humangenber", mtbDate));
            noUpdateRequired = false;
        }

        if (mtbReference != procedure.getValue("reftkreevaluation").getInt() && !mtbDate.equals(procedure.getValue("datumtkreevaluation").getDate())) {
            procedure.setValue("reftkreevaluation", new Item("ref_tk_reevaluation", mtbReference));
            procedure.setValue("datumtkreevaluation", new Item("datum_tk_reevaluation", mtbDate));
            noUpdateRequired = false;
        }

        if (noUpdateRequired) {
            return;
        }

        try {
            onkostarApi.saveProcedure(procedure, false);
        } catch (Exception e) {
            logger.error("Formular 'DNPM Therapieplan' konnte nicht aktualisiert werden", e);
        }
    }

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in Unterformularen
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    private void updateMtbInSubforms(Procedure procedure) {
        if (
                null != onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode")
                        && onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode").equals("true")
        ) {
            return;
        }

        var mtbReference = procedure.getValue("referstemtb").getInt();
        var mtbDate = procedure.getValue("datum").getDate();

        formService.getSubFormProcedureIds(procedure.getId()).stream()
                .map(onkostarApi::getProcedure)
                .filter(Objects::nonNull)
                .forEach(subform -> {
                    if (subform.getFormName().equals("DNPM UF Einzelempfehlung")) {
                        if (mtbReference != subform.getValue("mtb").getInt() && !mtbDate.equals(subform.getValue("ufeedatum").getDate())) {
                            subform.setValue("mtb", new Item("ref_tumorkonferenz", mtbReference));
                            subform.setValue("ufeedatum", new Item("datum", mtbDate));

                            try {
                                onkostarApi.saveProcedure(subform, false);
                            } catch (Exception e) {
                                logger.error("Formular 'DNPM UF Einzelempfehlung' konnte nicht aktualisiert werden", e);
                            }
                        }
                    }

                    if (subform.getFormName().equals("DNPM UF Rebiopsie")) {
                        if (mtbReference != subform.getValue("reftumorkonferenz").getInt() && !mtbDate.equals(subform.getValue("ufrbdatum").getDate())) {
                            subform.setValue("reftumorkonferenz", new Item("ref_tumorkonferenz", mtbReference));
                            subform.setValue("ufrbdatum", new Item("datum", mtbDate));

                            try {
                                onkostarApi.saveProcedure(subform, false);
                            } catch (Exception e) {
                                logger.error("Formular 'DNPM UF Rebiopsie' konnte nicht aktualisiert werden", e);
                            }
                        }
                    }
                });
    }
}
