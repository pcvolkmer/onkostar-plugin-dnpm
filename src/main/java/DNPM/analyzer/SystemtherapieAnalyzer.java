package DNPM.analyzer;

import DNPM.services.systemtherapie.SystemtherapieService;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Diese Klasse implementiert ein Plugin, welches Aktionen nach Bearbeitung eines Formulars zur Systemtherapie durchführt.
 *
 * @since 0.4.0
 */
@Component
public class SystemtherapieAnalyzer implements IProcedureAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    private final SystemtherapieService systemtherapieService;

    public SystemtherapieAnalyzer(
            final IOnkostarApi onkostarApi,
            final SystemtherapieService systemtherapieService
    ) {
        this.onkostarApi = onkostarApi;
        this.systemtherapieService = systemtherapieService;
    }

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

    @Override
    public String getVersion() {
        return "0.4.0";
    }

    @Override
    public String getName() {
        return "DNPM Systemtherapie Analyzer";
    }

    @Override
    public String getDescription() {
        return "Aktualisiert verknüpfte Formulare nach Änderungen im Formularen vom Typ Systemtherapie";
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isRelevantForDeletedProcedure() {
        return true;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return null != procedure && (
                procedure.getFormName().equals("OS.Systemische Therapie")
                || procedure.getFormName().equals("OS.Systemische Therapie.VarianteUKW")
        );
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
        var date = procedure.getStartDate();
        var status = procedure.getValue("ECOGvorTherapie");

        if (null == date || null == status) {
            // Ignore
            return;
        }

        var ecogFromCompleted = systemtherapieService.ecogSatus(procedure.getPatient());

        procedure.getPatient().getDiseases().stream()
                .flatMap(d -> onkostarApi.getProceduresForDiseaseByForm(d.getId(), "DNPM Klinik/Anamnese").stream())
                .forEach(p -> {
                    var ufEcog = p.getValue("ECOGVerlauf");
                    if (null != ufEcog && ufEcog.getValue() instanceof List) {
                        var shouldSave = false;
                        var existingDates = ((List<Map<String, Object>>) ufEcog.getValue()).stream()
                                .map(v -> v.get("Datum").toString())
                                .collect(Collectors.toList());
                        for (var ecog : ecogFromCompleted) {
                            var formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(ecog.getDate());
                            if (! existingDates.contains(formattedDate)) {
                                var newSubProcedure = new Procedure(onkostarApi);
                                newSubProcedure.setStartDate(ecog.getDate());
                                newSubProcedure.setValue("Datum", new Item("Datum", ecog.getDate()));
                                newSubProcedure.setValue("ECOG", new Item("ECOG", ecog.getStatus()));
                                p.addSubProcedure("ECOGVerlauf", newSubProcedure);
                                shouldSave = true;
                            }
                        }
                        if (shouldSave) {
                            try {
                                onkostarApi.saveProcedure(p, true);
                            } catch (Exception e) {
                                logger.error("Cannot update ECOG for procedure '{}'", p.getId());
                            }
                        }
                    }
                });
    }

}
