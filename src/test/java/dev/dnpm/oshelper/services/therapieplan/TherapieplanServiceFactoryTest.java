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

import de.itc.onkostar.api.IOnkostarApi;
import dev.dnpm.oshelper.services.FormService;
import dev.dnpm.oshelper.services.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TherapieplanServiceFactoryTest {

    private SettingsService settingsService;

    private TherapieplanServiceFactory therapieplanServiceFactory;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi,
            @Mock SettingsService settingsService,
            @Mock FormService formService
    ) {
        this.settingsService = settingsService;
        this.therapieplanServiceFactory = new TherapieplanServiceFactory(onkostarApi, settingsService, formService);
    }

    @Test
    void testShouldAlwaysReturnDefaultTherapieplanService() {
        var actual = this.therapieplanServiceFactory.currentUsableInstance();
        assertThat(actual).isInstanceOf(DefaultTherapieplanService.class);
    }
}
