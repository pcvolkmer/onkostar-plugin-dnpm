package DNPM.forms;

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
import java.util.concurrent.atomic.AtomicBoolean;

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
        updateMtbInSubforms(procedure);
    }

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in Unterformularen
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    // TODO: 13.03.23 Nicht ausführen, wenn durch Einstellung verboten
    // TODO: 13.03.23 Onkostar führt nach Speicherung eines Unterformulars erneut eine Speicherung des Hauptformulars aus - ggf eigene Speicher-Methode ohne Verwendung der Onkostar-API implementieren.
    private void updateMtbInSubforms(Procedure procedure) {
        logger.info("Run 'updateMtbInSubforms'");
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
