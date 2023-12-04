/*
 * MIT License
 *
 * 2023 Comprehensive Cancer Center Mainfranken
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

package DNPM.services.therapieplan;

import DNPM.services.FormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static DNPM.services.FormService.hasValue;
import static DNPM.services.FormService.isYes;

public class MultipleMtbTherapieplanService extends AbstractTherapieplanService {

    public MultipleMtbTherapieplanService(final IOnkostarApi onkostarApi, final FormService formService) {
        super(onkostarApi, formService);
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
