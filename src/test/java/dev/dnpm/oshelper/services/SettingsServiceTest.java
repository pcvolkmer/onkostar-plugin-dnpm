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

package dev.dnpm.oshelper.services;

import de.itc.db.dnpm.Setting;
import dev.dnpm.oshelper.database.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    private SettingsRepository settingsRepository;

    private SettingsService service;

    @BeforeEach
    void setUp(
            @Mock SettingsRepository settingsRepository
    ) {
        this.settingsRepository = settingsRepository;
        this.service = new SettingsService(settingsRepository);
    }

    @Test
    void shouldReturnSID() {
        doAnswer(invocationOnMock -> {
            var name = invocationOnMock.getArgument(0, String.class);
            if (null != name && name.equals("SID")) {
                return new Setting(1L, "SID", "12345");
            }
            return null;
        }).when(settingsRepository).findByName(anyString());

        var actual = service.getSID();
        assertThat(actual)
                .isPresent()
                .contains("12345");
    }

    @Test
    void shouldReturnSIDByName() {
        doAnswer(invocationOnMock -> {
            var name = invocationOnMock.getArgument(0, String.class);
            if (null != name && name.equals("SID")) {
                return new Setting(1L, "SID", "12345");
            }
            return null;
        }).when(settingsRepository).findByName(anyString());

        var actual = service.getSetting("SID");
        assertThat(actual)
                .isPresent()
                .contains("12345");
    }

}
