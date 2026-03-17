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

import dev.dnpm.oshelper.dto.Studie;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudieTest {

    @Test
    void shouldDetectStudieWithNctNumber() {
        var studie = new Studie(
                "Kat 1",
                1,
                null,
                "Nct-12345678",
                "Teststudie 1",
                "Teststudie 1",
                true
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.NCT);
    }

    @Test
    void shouldDetectStudieWithEudraCtNumber() {
        var studie = new Studie(
                "Kat 1",
                1,
                null,
                "2023-012345-12",
                "Teststudie 1",
                "Teststudie 1",
                true
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.EUDRA_CT);
    }

    @Test
    void shouldReturnStudieWithUnknownNumberScheme() {
        var studie = new Studie(
                "Kat 1",
                1,
                "teststudie1",
                null,
                "Teststudie 1",
                "Teststudie 1",
                true
        );

        assertThat(studie.getType()).isEqualTo(Studie.Type.UNKNOWN);
    }

}
