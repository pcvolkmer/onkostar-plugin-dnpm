package DNPM.services.therapieplan;

import DNPM.services.FormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static DNPM.services.FormService.hasValue;
import static DNPM.services.FormService.isYes;

public class DefaultTherapieplanService extends AbstractTherapieplanService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DefaultTherapieplanService(final IOnkostarApi onkostarApi, final FormService formService) {
        super(onkostarApi, formService);
    }

    /**
     * Verlinke MTB und Übernahme Datum aus Hauptformular in weiteren Bereichen
     * "Humangenetische Beratung" und "Reevaluation" und Unterformularen, wenn erforderlich.
     *
     * @param procedure Die Prozedur mit Hauptformular
     */
    @Override
    public void updateRequiredMtbEntries(Procedure procedure) {
        this.updateMtbInSections(procedure);
        this.updateMtbInSubforms(procedure);
    }

    /**
     * Finde verlinkte MTBs in Hauptformular und Unterformularen
     *
     * @param procedure Die Prozedur mit Hauptformular
     * @return Liste mit verlinkten MTBs
     */
    @Override
    public List<Procedure> findReferencedMtbs(Procedure procedure) {
        if (!hasValue(procedure, "referstemtb")) {
            return List.of();
        }

        var mtbProcedure = this.onkostarApi.getProcedure(procedure.getValue("referstemtb").getInt());
        if (null == mtbProcedure) {
            return List.of();
        }
        return List.of(mtbProcedure);
    }

    /**
     * Finde verlinkte MTBs in Hauptformular und Unterformularen
     *
     * @param procedureId ID der Prozedur mit Hauptformular
     * @return Liste mit verlinkten MTBs
     */
    @Override
    public List<Procedure> findReferencedMtbs(int procedureId) {
        var procedure = this.onkostarApi.getProcedure(procedureId);
        if (null == procedure) {
            return List.of();
        }
        return findReferencedMtbs(procedure);
    }

    private void updateMtbInSections(Procedure procedure) {
        if (!isYes(procedure, "humangenberatung") && !isYes(procedure, "reevaluation")) {
            return;
        }

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

    private void updateMtbInSubforms(Procedure procedure) {
        if (
                !hasValue(procedure, "referstemtb") || !hasValue(procedure, "datum")
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
