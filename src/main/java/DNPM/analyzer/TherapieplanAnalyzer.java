package DNPM.analyzer;

import DNPM.services.Studie;
import DNPM.services.StudienService;
import DNPM.services.TherapieplanService;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Diese Klasse implementiert ein Plugin, welches Aktionen nach Bearbeitung eines Therapieplans durchführt.
 *
 * @since 0.0.2
 */
@Component
public class TherapieplanAnalyzer implements IProcedureAnalyzer {

    private final StudienService studienService;

    private final TherapieplanService therapieplanService;

    public TherapieplanAnalyzer(
            final StudienService studienService,
            final TherapieplanService therapieplanService
    ) {
        this.studienService = studienService;
        this.therapieplanService = therapieplanService;
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
        therapieplanService.updateRequiredMtbEntries(procedure);
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

}
