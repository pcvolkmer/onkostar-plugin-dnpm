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

import dev.dnpm.services.consent.ConsentManagerServiceFactory;
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
