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

package dev.dnpm.oshelper.atc;

import dev.dnpm.oshelper.atc.services.AgentCodeService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AgentCodeServiceTest {

    @Test
    void shouldReturnLatestAvailableAgentCodeVersion() {
        var testAgentCodes = List.of(
                new AtcCode("A01AD05", "Acetylsalicylsäure"),
                new AtcCode("A01AD05", "Acetylsalicylsäure", "2025"),
                new AtcCode("A01AD05", "Acetylsalicylsäure", "2026"),
                new UnregisteredCode("A01AD05", "Acetylsalicylsäure"),
                new UnregisteredCode("", "Acetylsalicylsäure")
        );

        var actual = AgentCodeService.latestAgentCodeVersions(testAgentCodes, AgentCodeService.byNameAndCode);

        assertThat(actual).containsAll(
                List.of(
                        new AtcCode("A01AD05", "Acetylsalicylsäure", "2026"),
                        new UnregisteredCode("", "Acetylsalicylsäure")
                )
        );
    }

    @Test
    void shouldReturnLatestAvailableAgentCodeVersionByNameOnly() {
        var testAgentCodes = List.of(
                new AtcCode("A01AD05", "Acetylsalicylsäure"),
                new AtcCode("A01AD05", "Acetylsalicylsäure", "2025"),
                new AtcCode("A01AD05", "Acetylsalicylsäure", "2026"),
                new UnregisteredCode("A01AD05", "Acetylsalicylsäure"),
                new UnregisteredCode("", "Acetylsalicylsäure")
        );

        var actual = AgentCodeService.latestAgentCodeVersions(testAgentCodes, AgentCodeService.byCodeOnly);

        assertThat(actual).containsExactly(
                new AtcCode("A01AD05", "Acetylsalicylsäure", "2026")
        );
    }

    @Test
    void shouldReturnLatestAvailableAgentCodeWithoutVersionVersionByNameOnly() {
        var testAgentCodes = List.of(
                new UnregisteredCode("A01AD05", "Acetylsalicylsäure"),
                new AtcCode("A01AD05", "Acetylsalicylsäure"),
                new UnregisteredCode("", "Acetylsalicylsäure")
        );

        var actual = AgentCodeService.latestAgentCodeVersions(testAgentCodes, AgentCodeService.byCodeOnly);

        assertThat(actual).containsExactly(
                new AtcCode("A01AD05", "Acetylsalicylsäure")
        );
    }

}
