/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (C) 2023-2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.oshelper.analyzer;

import dev.dnpm.oshelper.services.consent.ConsentManagerServiceFactory;
import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsentManager extends Analyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    private final ConsentManagerServiceFactory consentManagerServiceFactory;

    public ConsentManager(
            final IOnkostarApi onkostarApi,
            final ConsentManagerServiceFactory consentManagerServiceFactory
    ) {
        this.onkostarApi = onkostarApi;
        this.consentManagerServiceFactory = consentManagerServiceFactory;
    }

    @Override
    public String getDescription() {
        return "Aktualisiert Consent Daten in verknüpften Formularen";
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure prozedur, Disease erkrankung) {
        return prozedur.getFormName().equals(onkostarApi.getGlobalSetting("consentform"));
    }

    @Override
    public boolean isRelevantForDeletedProcedure() {
        // TODO is relevant for deleted procedure = true
        return false;
    }

    @Override
    public boolean isSynchronous() {
        return true;
    }

    @Override
    public void analyze(Procedure prozedur, Disease erkrankung) {
        var consentManagerService = consentManagerServiceFactory.currentUsableInstance();
        if (! consentManagerService.canApply(prozedur)) {
            logger.error("Fehler im ConsentManagement: Kann Prozedur mit Formularnamen '{}' nicht anwenden", prozedur.getFormName());
            return;
        }
        consentManagerService.applyConsent(prozedur);
    }

}
