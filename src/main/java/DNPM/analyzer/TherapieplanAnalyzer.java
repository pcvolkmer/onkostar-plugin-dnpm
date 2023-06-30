package DNPM.analyzer;

import DNPM.security.DelegatingDataBasedPermissionEvaluator;
import DNPM.security.PermissionType;
import DNPM.services.mtb.MtbService;
import DNPM.services.therapieplan.TherapieplanServiceFactory;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Diese Klasse implementiert ein Plugin, welches Aktionen nach Bearbeitung eines Therapieplans durchführt.
 *
 * @since 0.0.2
 */
@Component
public class TherapieplanAnalyzer implements IProcedureAnalyzer {

    private final TherapieplanServiceFactory therapieplanServiceFactory;

    private final MtbService mtbService;

    private final DelegatingDataBasedPermissionEvaluator permissionEvaluator;

    public TherapieplanAnalyzer(
            final TherapieplanServiceFactory therapieplanServiceFactory,
            final MtbService mtbService,
            final DelegatingDataBasedPermissionEvaluator permissionEvaluator
    ) {
        this.therapieplanServiceFactory = therapieplanServiceFactory;
        this.mtbService = mtbService;
        this.permissionEvaluator = permissionEvaluator;
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
        therapieplanServiceFactory.currentUsableInstance().updateRequiredMtbEntries(procedure);
    }

    /**
     * Übergibt den Text der referenzierten MTBs für den Protokollauszug
     *
     * <p>Wurde der Eingabewert <code>id</code> nicht übergeben, wird ein leerer String zurück gegeben.
     *
     * <p>Beispiel zur Nutzung in einem Formularscript
     * <pre>
     * executePluginMethod(
     *   'TherapieplanAnalyzer',
     *   'getProtokollauszug',
     *   { id: 12345 },
     *   (response) => console.log(response),
     *   false
     * );
     * </pre>
     *
     * @param input Map mit Eingabewerten
     * @return Zeichenkette mit Protokollauszug
     */
    public String getProtokollauszug(Map<String, Object> input) {
        var procedureId = AnalyzerUtils.getRequiredId(input, "id");

        if (procedureId.isEmpty()) {
            return "";
        }

        if (
                permissionEvaluator.hasPermission(
                        SecurityContextHolder.getContext().getAuthentication(),
                        procedureId.get(),
                        Procedure.class.getSimpleName(),
                        PermissionType.READ
                )
        ) {
            return mtbService.getProtocol(
                    therapieplanServiceFactory
                            .currentUsableInstance()
                            .findReferencedMtbs(procedureId.get())
            );
        }

        return "";
    }

}
