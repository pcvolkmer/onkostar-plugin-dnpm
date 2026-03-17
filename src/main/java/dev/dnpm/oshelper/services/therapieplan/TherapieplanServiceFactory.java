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

package dev.dnpm.oshelper.services.therapieplan;

import dev.dnpm.oshelper.services.FormService;
import dev.dnpm.oshelper.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;

public class TherapieplanServiceFactory {

    private final IOnkostarApi onkostarApi;

    private final FormService formService;

    @SuppressWarnings("unused")
    public TherapieplanServiceFactory(
            final IOnkostarApi onkostarApi,
            final SettingsService settingsService,
            final FormService formService
    ) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
    }

    public TherapieplanService currentUsableInstance() {
        return new DefaultTherapieplanService(onkostarApi, formService);
    }

}
