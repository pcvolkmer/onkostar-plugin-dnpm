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

package dev.dnpm.analyzer;

import dev.dnpm.dto.EcogStatusWithDate;
import dev.dnpm.services.strahlentherapie.StrahlentherapieService;
import dev.dnpm.services.systemtherapie.SystemtherapieService;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyseTriggerEvent;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
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
 * @since 0.6.0
 */
@Component
public class TherapieMitEcogAnalyzer extends Analyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    private final StrahlentherapieService strahlentherapieService;
    private final SystemtherapieService systemtherapieService;

    public TherapieMitEcogAnalyzer(
            final IOnkostarApi onkostarApi,
            final StrahlentherapieService strahlentherapieService,
            final SystemtherapieService systemtherapieService
    ) {
        this.onkostarApi = onkostarApi;
        this.strahlentherapieService = strahlentherapieService;
        this.systemtherapieService = systemtherapieService;
    }

    @Override
    public String getDescription() {
        return "Aktualisiert verknüpfte Formulare nach Änderungen in Formularen vom Typ Strahlen-/Systemtherapie mit ECOG-Status";
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
        return null != procedure && null != disease && (
                procedure.getFormName().equals("OS.Strahlentherapie")
                        || procedure.getFormName().equals("OS.Strahlentherapie.VarianteUKW")
                        || procedure.getFormName().equals("OS.Systemische Therapie")
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

        var ecog = strahlentherapieService.ecogStatus(procedure.getPatient())
                .stream()
                .filter(ecogStatusWithDate -> ecogStatusWithDate.getDate().after(disease.getDiagnosisDate()))
                .collect(Collectors.toList());

        ecog.addAll(systemtherapieService.ecogStatus(procedure.getPatient())
                .stream()
                .filter(ecogStatusWithDate -> ecogStatusWithDate.getDate().after(disease.getDiagnosisDate()))
                .collect(Collectors.toList()));


        if (ecog.isEmpty()) {
            // Nothing to do
            return;
        }

        procedure.getPatient().getDiseases().stream()
                .flatMap(d -> onkostarApi.getProceduresForDiseaseByForm(d.getId(), "DNPM Klinik/Anamnese").stream())
                .forEach(p -> {
                    var ufEcog = p.getValue("ECOGVerlauf");
                    if (null != ufEcog && ufEcog.getValue() instanceof List) {
                        updateExistingEcogVerlauf(p, ecog, ufEcog);
                    } else {
                        newEcogverlauf(p, ecog);
                    }
                });
    }

    private void updateExistingEcogVerlauf(Procedure p, List<EcogStatusWithDate> ecogFromCompleted, Item ufEcog) {
        var shouldSave = false;
        var existingDates = ufEcog.<List<Map<String, String>>>getValue().stream()
                .map(v -> v.get("Datum"))
                .collect(Collectors.toList());
        for (var ecog : ecogFromCompleted) {
            var formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(ecog.getDate());
            if (!existingDates.contains(formattedDate)) {
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

    private void newEcogverlauf(Procedure p, List<EcogStatusWithDate> ecogFromCompleted) {
        p.setValue("ECOGVerlauf", new Item("ECOGVerlauf", List.of()));
        for (var ecog : ecogFromCompleted) {
            var newSubProcedure = new Procedure(onkostarApi);
            newSubProcedure.setStartDate(ecog.getDate());
            newSubProcedure.setValue("Datum", new Item("Datum", ecog.getDate()));
            newSubProcedure.setValue("ECOG", new Item("ECOG", ecog.getStatus()));
            p.addSubProcedure("ECOGVerlauf", newSubProcedure);
        }
        try {
            onkostarApi.saveProcedure(p, true);
        } catch (Exception e) {
            logger.error("Create update ECOG for procedure '{}'", p.getId());
        }
    }

}
