package DNPM.analyzer;

import DNPM.services.FormService;
import DNPM.services.Studie;
import DNPM.services.StudienService;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import de.itc.onkostar.api.constants.JaNeinUnbekannt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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

    private final StudienService studienService;

    public TherapieplanAnalyzer(final IOnkostarApi onkostarApi, final FormService formService, final StudienService studienService) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
        this.studienService = studienService;
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
     * Übergibt alle Studien, deren (Kurz-)Beschreibung oder NCT-Nummer den übergebenen Eingabewert <code>q</code> enthält
     *
     * <p>Wurde der Eingabewert nicht angegeben oder ist leer, werden alle Studien übergeben.
     *
     * <p>Beispiel zur Nutzung in einem Formularscript
     * <pre>
     * executePluginMethod(
     *   'TherapieplanAnalyzer',
     *   'getStudien',
     *   { q: 'NCT-12' },
     *   (response) => console.log(response),
     *   false
     * );
     * </pre>
     * @param input Map mit Eingabewerten
     */
    public List<Studie> getStudien(Map<String, Object> input) {
        var query = input.get("q");

        if (null == query || query.toString().isBlank()) {
            return studienService.findAll();
        }
        return studienService.findByQuery(query.toString());
    }

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in weiteren Bereichen
     * "Humangenetische Beratung" und "Reevaluation", wenn erforderlich.
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    private void updateMtbInSections(Procedure procedure) {
        if (
                this.hasMultipleMtbsEnabled() || (
                        !isYes(procedure, "humangenberatung") && !isYes(procedure, "reevaluation")
                )
        ) {
            return;
        }

        var a = procedure.getValue("humangenberatung").getString();
        var b = procedure.getValue("reevaluation").getString();

        var mtbReference = procedure.getValue("referstemtb").getInt();
        var mtbDate = procedure.getValue("datum").getDate();
        var noUpdateRequired = true;

        if (
                isYes(procedure, "humangenberatung") && (
                        !hasValue(procedure, "reftkhumangenber")
                                || mtbReference != procedure.getValue("reftkhumangenber").getInt()
                )
        ) {
            procedure.setValue("reftkhumangenber", new Item("ref_tk_humangenber", mtbReference));
            noUpdateRequired = false;
        }

        if (
                isYes(procedure, "humangenberatung") && (
                        !hasValue(procedure, "datumtkhumangenber")
                                || !mtbDate.equals(procedure.getValue("datumtkhumangenber").getDate())
                )
        ) {
            procedure.setValue("datumtkhumangenber", new Item("datum_tk_humangenber", mtbDate));
            noUpdateRequired = false;
        }

        if (
                isYes(procedure, "reevaluation") && (
                        !hasValue(procedure, "reftkreevaluation")
                                || mtbReference != procedure.getValue("reftkreevaluation").getInt()
                )
        ) {
            procedure.setValue("reftkreevaluation", new Item("ref_tk_reevaluation", mtbReference));
            noUpdateRequired = false;
        }

        if (
                isYes(procedure, "reevaluation") && (
                        !hasValue(procedure, "datumtkreevaluation")
                                || !mtbDate.equals(procedure.getValue("datumtkreevaluation").getDate())
                )
        ) {
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
        if (this.hasMultipleMtbsEnabled()) {
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

    private boolean hasMultipleMtbsEnabled() {
        return null != onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode")
                && onkostarApi.getGlobalSetting("mehrere_mtb_in_mtbepisode").equals("true");
    }

    private boolean hasValue(final Procedure procedure, final String fieldName) {
        return null != procedure.getValue(fieldName);
    }

    private boolean isYes(final Procedure procedure, final String fieldName) {
        return hasValue(procedure, fieldName)
                && procedure.getValue(fieldName).getString().equals(JaNeinUnbekannt.JA.getCode());
    }
}
