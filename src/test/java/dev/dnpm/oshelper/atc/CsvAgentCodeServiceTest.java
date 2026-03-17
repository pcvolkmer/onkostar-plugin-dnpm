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
import dev.dnpm.oshelper.atc.services.CsvAgentCodeService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvAgentCodeServiceTest {

    AgentCodeService agentCodeService = new CsvAgentCodeService();

    @Test
    void shouldReturnAtcCodeFromEmbeddedResource() {
        var actual = agentCodeService.findAgentCodes("Cisplatin", 10);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getCode()).isEqualTo("L01XA01");
    }

}
