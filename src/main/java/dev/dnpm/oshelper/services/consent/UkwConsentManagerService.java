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

package dev.dnpm.oshelper.services.consent;

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
@Deprecated(forRemoval = true, since = "2.0.0")
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
