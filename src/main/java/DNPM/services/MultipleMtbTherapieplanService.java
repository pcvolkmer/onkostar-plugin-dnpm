package DNPM.services;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static DNPM.services.FormService.hasValue;
import static DNPM.services.FormService.isYes;

public class MultipleMtbTherapieplanService implements TherapieplanService {

    private final IOnkostarApi onkostarApi;

    private final FormService formService;

    public MultipleMtbTherapieplanService(final IOnkostarApi onkostarApi, final FormService formService) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
    }

    @Override
    public void updateRequiredMtbEntries(Procedure procedure) {
        // No action required
    }

    @Override
    public List<Procedure> findReferencedMtbs(Procedure procedure) {
        var procedureIds = new ArrayList<Integer>();

        var mtbReference = procedure.getValue("referstemtb").getInt();
        procedureIds.add(mtbReference);

        if (isYes(procedure, "humangenberatung") && hasValue(procedure, "reftkhumangenber")) {
            procedureIds.add(procedure.getValue("reftkhumangenber").getInt());
        }

        if (isYes(procedure, "reevaluation") && hasValue(procedure, "reftkreevaluation")) {
            procedureIds.add(procedure.getValue("reftkreevaluation").getInt());
        }

        formService.getSubFormProcedureIds(procedure.getId()).stream()
                .map(onkostarApi::getProcedure)
                .filter(Objects::nonNull)
                .forEach(subform -> {
                    if (subform.getFormName().equals("DNPM UF Einzelempfehlung")) {
                        procedureIds.add(subform.getValue("mtb").getInt());
                    }

                    if (subform.getFormName().equals("DNPM UF Rebiopsie")) {
                        procedureIds.add(subform.getValue("reftumorkonferenz").getInt());
                    }
                });

        return procedureIds.stream()
                .distinct()
                .map(onkostarApi::getProcedure)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Procedure::getStartDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Procedure> findReferencedMtbs(int procedureId) {
        var procedure = this.onkostarApi.getProcedure(procedureId);
        if (null == procedure) {
            return List.of();
        }
        return findReferencedMtbs(procedure);
    }
}
