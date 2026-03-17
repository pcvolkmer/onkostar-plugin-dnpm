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

@Deprecated(forRemoval = true, since = "2.0.0")
public class ConsentManagerServiceFactory {

    private final IOnkostarApi onkostarApi;

    public ConsentManagerServiceFactory(
            final IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
    }

    public ConsentManagerService currentUsableInstance() {
        var consentFormName = onkostarApi.getGlobalSetting("consentform");

        switch (consentFormName) {
            case "Excel-Formular":
                return new UkwConsentManagerService(this.onkostarApi);
            case "MR.Consent":
                return new MrConsentManagerService(this.onkostarApi);
            default:
                return procedure -> {};
        }
    }

}
