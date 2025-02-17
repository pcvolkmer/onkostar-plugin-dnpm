/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.dnpm.services.therapieplan;

import dev.dnpm.services.FormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static dev.dnpm.services.FormService.hasValue;
import static dev.dnpm.services.FormService.isYes;

public class DefaultTherapieplanService extends AbstractTherapieplanService {

    public static final String FORMFIELD_REFERSTEMTB = "referstemtb";
    public static final String FORMFIELD_HUMANGENBERATUNG = "humangenberatung";
    public static final String FORMFIELD_REEVALUATION = "reevaluation";
    public static final String FORMFIELD_DATUM = "datum";
    public static final String FORMFIELD_REFTKHUMANGENBER = "reftkhumangenber";
    public static final String FORMFIELD_DATUMTKHUMANGENBER = "datumtkhumangenber";
    public static final String FORMFIELD_REFTKREEVALUATION = "reftkreevaluation";
    public static final String FORMFIELD_DATUMTKREEVALUATION = "datumtkreevaluation";
    public static final String FORMFIELD_MTB = "mtb";
    public static final String FORMFIELD_UFEEDATUM = "ufeedatum";
    public static final String FORMFIELD_REFTUMORKONFERENZ = "reftumorkonferenz";
    public static final String FORMFIELD_UFRBDATUM = "ufrbdatum";

    public static final String DATAFIELD_REF_TK_HUMANGENBER = "ref_tk_humangenber";
    public static final String DATAFIELD_DATUM_TK_HUMANGENBER = "datum_tk_humangenber";
    public static final String DATAFIELD_DATUM = "datum";

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
        if (!hasValue(procedure, FORMFIELD_REFERSTEMTB)) {
            return List.of();
        }

        var mtbProcedure = this.onkostarApi.getProcedure(procedure.getValue(FORMFIELD_REFERSTEMTB).getInt());
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
        if (!isYes(procedure, FORMFIELD_HUMANGENBERATUNG) && !isYes(procedure, FORMFIELD_REEVALUATION)) {
            return;
        }

        var mtbReference = procedure.getValue(FORMFIELD_REFERSTEMTB).getInt();
        var mtbDate = procedure.getValue(FORMFIELD_DATUM).getDate();
        var noUpdateRequired = true;

        if (
                isYes(procedure, FORMFIELD_HUMANGENBERATUNG) && (
                        !hasValue(procedure, FORMFIELD_REFTKHUMANGENBER)
                                || mtbReference != procedure.getValue(FORMFIELD_REFTKHUMANGENBER).getInt()
                )
        ) {
            procedure.setValue(FORMFIELD_REFTKHUMANGENBER, new Item(DATAFIELD_REF_TK_HUMANGENBER, mtbReference));
            noUpdateRequired = false;
        }

        if (
                isYes(procedure, FORMFIELD_HUMANGENBERATUNG) && (
                        !hasValue(procedure, FORMFIELD_DATUMTKHUMANGENBER)
                                || !mtbDate.equals(procedure.getValue(FORMFIELD_DATUMTKHUMANGENBER).getDate())
                )
        ) {
            procedure.setValue(FORMFIELD_DATUMTKHUMANGENBER, new Item(DATAFIELD_DATUM_TK_HUMANGENBER, mtbDate));
            noUpdateRequired = false;
        }

        if (
                isYes(procedure, FORMFIELD_REEVALUATION) && (
                        !hasValue(procedure, FORMFIELD_REFTKREEVALUATION)
                                || mtbReference != procedure.getValue(FORMFIELD_REFTKREEVALUATION).getInt()
                )
        ) {
            procedure.setValue(FORMFIELD_REFTKREEVALUATION, new Item("ref_tk_reevaluation", mtbReference));
            noUpdateRequired = false;
        }

        if (
                isYes(procedure, FORMFIELD_REEVALUATION) && (
                        !hasValue(procedure, FORMFIELD_DATUMTKREEVALUATION)
                                || !mtbDate.equals(procedure.getValue(FORMFIELD_DATUMTKREEVALUATION).getDate())
                )
        ) {
            procedure.setValue(FORMFIELD_DATUMTKREEVALUATION, new Item("datum_tk_reevaluation", mtbDate));
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
                !hasValue(procedure, FORMFIELD_REFERSTEMTB) || !hasValue(procedure, FORMFIELD_DATUM)
        ) {
            return;
        }

        var mtbReference = procedure.getValue(FORMFIELD_REFERSTEMTB).getInt();
        var mtbDate = procedure.getValue(FORMFIELD_DATUM).getDate();

        formService.getSubFormProcedureIds(procedure.getId()).stream()
                .map(onkostarApi::getProcedure)
                .filter(Objects::nonNull)
                .forEach(subform -> {
                    if (isUsableEinzelempfehlung(subform, mtbReference, mtbDate)) {
                        subform.setValue(FORMFIELD_MTB, new Item("ref_tumorkonferenz", mtbReference));
                        subform.setValue(FORMFIELD_UFEEDATUM, new Item(DATAFIELD_DATUM, mtbDate));

                        try {
                            onkostarApi.saveProcedure(subform, false);
                        } catch (Exception e) {
                            logger.error("Formular 'DNPM UF Einzelempfehlung' konnte nicht aktualisiert werden", e);
                        }
                    }


                    if (isUsableRebiopsie(subform, mtbReference, mtbDate)) {
                        subform.setValue(FORMFIELD_REFTUMORKONFERENZ, new Item("ref_tumorkonferenz", mtbReference));
                        subform.setValue(FORMFIELD_UFRBDATUM, new Item(DATAFIELD_DATUM, mtbDate));

                        try {
                            onkostarApi.saveProcedure(subform, false);
                        } catch (Exception e) {
                            logger.error("Formular 'DNPM UF Rebiopsie' konnte nicht aktualisiert werden", e);
                        }
                    }

                });
    }

    private static boolean isUsableRebiopsie(Procedure subform, int mtbReference, Date mtbDate) {
        return subform.getFormName().equals("DNPM UF Rebiopsie") && mtbReference != subform.getValue(FORMFIELD_REFTUMORKONFERENZ).getInt() && !mtbDate.equals(subform.getValue(FORMFIELD_UFRBDATUM).getDate());
    }

    private static boolean isUsableEinzelempfehlung(Procedure subform, int mtbReference, Date mtbDate) {
        return subform.getFormName().equals("DNPM UF Einzelempfehlung") && mtbReference != subform.getValue(FORMFIELD_MTB).getInt() && !mtbDate.equals(subform.getValue(FORMFIELD_UFEEDATUM).getDate());
    }

}
