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

package dev.dnpm.services.consent;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Detailimplementierung für das Formular `Excel-Formular`
 *
 * @since 0.2.0
 */
public class UkwConsentManagerService implements ConsentManagerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IOnkostarApi onkostarApi;

    public UkwConsentManagerService(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    @Override
    public boolean canApply(Procedure procedure) {
        return null != procedure && procedure.getFormName().equals("Excel-Formular");
    }

    /**
     * Wende Consent an, wenn dieses Consent-Formular gespeichert wird
     *
     * @param procedure Prozedur des Consent-Formulars
     */
    @Override
    public void applyConsent(Procedure procedure) {
        var refdnpmklinikanamnese = procedure.getValue("refdnpmklinikanamnese").getInt();
        var dnpmKlinikAnamnese = this.onkostarApi.getProcedure(refdnpmklinikanamnese);

        if (null == dnpmKlinikAnamnese) {
            return;
        }

        var consents = procedure.getSubProceduresMap().get("ufdnpmconsent");

        if (null == consents) {
            return;
        }

        consents.stream()
                .max(Comparator.comparing(Procedure::getStartDate))
                .ifPresent(lastConsent -> {
                    var date = lastConsent.getStartDate();
                    var status = lastConsent.getValue("status");
                    if (null == date || null == status || status.getString().isBlank()) {
                        logger.warn("Kein DNPM-Einwilligungstatus angegeben");
                        return;
                    }

                    dnpmKlinikAnamnese.setValue("ConsentStatusEinwilligungDNPM", new Item("Einwilligung", status.getString()));
                    dnpmKlinikAnamnese.setValue("ConsentDatumEinwilligungDNPM", new Item("DatumEinwilligung", date));

                    try {
                        onkostarApi.saveProcedure(dnpmKlinikAnamnese, false);
                    } catch (Exception e) {
                        logger.error("Kann DNPM-Einwilligungstatus nicht aktualisieren", e);
                    }
                });
    }

}
