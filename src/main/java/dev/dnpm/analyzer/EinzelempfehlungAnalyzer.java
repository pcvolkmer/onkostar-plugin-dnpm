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

import dev.dnpm.dto.Studie;
import dev.dnpm.dto.Variant;
import dev.dnpm.security.PermissionType;
import dev.dnpm.security.PersonPoolBasedPermissionEvaluator;
import dev.dnpm.services.StudienService;
import dev.dnpm.services.molekulargenetik.MolekulargenetikFormService;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert ein Plugin, welches Funktionen für DNPM UF Einzelempfehlung bereitstellt.
 *
 * @since 0.2.0
 */
@Component
public class EinzelempfehlungAnalyzer extends BackendService {

    private final static Logger logger = LoggerFactory.getLogger(EinzelempfehlungAnalyzer.class);

    private final IOnkostarApi onkostarApi;

    private final MolekulargenetikFormService molekulargenetikFormService;

    private final StudienService studienService;

    private final PersonPoolBasedPermissionEvaluator permissionEvaluator;

    public EinzelempfehlungAnalyzer(
            final IOnkostarApi onkostarApi,
            final StudienService studienService,
            final MolekulargenetikFormService molekulargenetikFormService,
            final PersonPoolBasedPermissionEvaluator permissionEvaluator
    ) {
        this.onkostarApi = onkostarApi;
        this.studienService = studienService;
        this.molekulargenetikFormService = molekulargenetikFormService;
        this.permissionEvaluator = permissionEvaluator;
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

    public List<Variant> getVariants(Map<String, Object> input) {
        var procedureId = AnalyzerUtils.getRequiredId(input, "id");

        if (procedureId.isEmpty()) {
            return List.of();
        }

        var procedure = onkostarApi.getProcedure(procedureId.get());
        if (null == procedure) {
            return List.of();
        }

        if (permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), procedure, PermissionType.READ)) {
            return molekulargenetikFormService.getVariants(procedure);
        } else {
            logger.error("Security: No permission to access procedure '{}'", procedure.getId());
            return List.of();
        }
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
     *   { q: 'NCT-12', inactive: true },
     *   (response) => console.log(response),
     *   false
     * );
     * </pre>
     *
     * @param input Map mit Eingabewerten
     * @return Liste mit Studien
     */
    public List<Studie> getStudien(Map<String, Object> input) {
        var query = AnalyzerUtils.getRequiredValue(input, "q", String.class);
        var inactive = AnalyzerUtils.getRequiredValue(input, "inactive", Boolean.class).orElse(false);

        if (query.isEmpty() || query.get().isBlank()) {
            if (inactive) {
                return studienService.findAll();
            }
            return studienService.findActive();
        }
        if (inactive) {
            return studienService.findByQuery(query.get());
        }
        return studienService.findActiveByQuery(query.get());
    }

}
