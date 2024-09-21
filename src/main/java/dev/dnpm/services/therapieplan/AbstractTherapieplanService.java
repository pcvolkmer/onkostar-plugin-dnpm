package dev.dnpm.services.therapieplan;

import dev.dnpm.services.FormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTherapieplanService implements TherapieplanService {

    protected final IOnkostarApi onkostarApi;

    protected final FormService formService;

    protected AbstractTherapieplanService(final IOnkostarApi onkostarApi, final FormService formService) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
    }

    @Override
    public List<Procedure> findReferencedFollowUpsForSubform(Procedure procedure) {
        if (null == procedure || !"DNPM UF Einzelempfehlung".equals(procedure.getFormName())) {
            return List.of();
        }

        return procedure.getDiseaseIds().stream()
                .flatMap(diseaseId -> onkostarApi.getProceduresForDiseaseByForm(diseaseId, "DNPM FollowUp").stream())
                .filter(p -> p.getValue("LinkTherapieempfehlung").getInt() == procedure.getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Procedure> findReferencedFollowUpsForSubform(int procedureId) {
        var procedure = this.onkostarApi.getProcedure(procedureId);
        if (null == procedure || !"DNPM UF Einzelempfehlung".equals(procedure.getFormName())) {
            return List.of();
        }
        return findReferencedFollowUpsForSubform(procedure);
    }

}
