package DNPM.analyzer;

import DNPM.dto.Variant;
import DNPM.security.DelegatingDataBasedPermissionEvaluator;
import DNPM.security.IllegalSecuredObjectAccessException;
import DNPM.services.molekulargenetik.MolekulargenetikFormService;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import de.itc.onkostar.api.analysis.IProcedureAnalyzer;
import de.itc.onkostar.api.analysis.OnkostarPluginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert ein Plugin, welches Funktionen für DNPM UF Einzelempfehlung bereit stellt.
 *
 * @since 0.2.0
 */
@Component
public class EinzelempfehlungAnalyzer implements IProcedureAnalyzer {

    private final static Logger logger = LoggerFactory.getLogger(EinzelempfehlungAnalyzer.class);

    private final IOnkostarApi onkostarApi;

    private final MolekulargenetikFormService molekulargenetikFormService;

    private final DelegatingDataBasedPermissionEvaluator permissionEvaluator;

    public EinzelempfehlungAnalyzer(
            final IOnkostarApi onkostarApi,
            final MolekulargenetikFormService molekulargenetikFormService,
            final DelegatingDataBasedPermissionEvaluator permissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.molekulargenetikFormService = molekulargenetikFormService;
        this.permissionEvaluator = permissionEvaluator;
    }

    @Override
    public OnkostarPluginType getType() {
        return OnkostarPluginType.BACKEND_SERVICE;
    }

    @Override
    public String getVersion() {
        return "0.1.0";
    }

    @Override
    public String getName() {
        return "DNPM Einzelempfehlung Backend Service";
    }

    @Override
    public String getDescription() {
        return "Stellt Funktionen zur Nutzung im Therapieplan-Unterformular für Einzelempfehlungen bereit";
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
        return false;
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
    public void analyze(Procedure procedure, Disease disease) {
        // No op
    }

    public List<Variant> getVariants(Map<String, Object> input) {
        var procedureId = AnalyzerUtils.getRequiredId(input, "id");

        if (procedureId.isEmpty()) {
            return List.of();
        }

        var procedure = onkostarApi.getProcedure(procedureId.get());
        if (null == procedure) {
            return List.of();
        }

        try {
            return molekulargenetikFormService.getVariants(procedure);
        } catch (IllegalSecuredObjectAccessException e) {
            logger.error("Security", e);
            return List.of();
        }
    }

}
