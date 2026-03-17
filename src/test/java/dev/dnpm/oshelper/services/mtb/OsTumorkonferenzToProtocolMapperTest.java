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

package dev.dnpm.oshelper.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OsTumorkonferenzToProtocolMapperTest {

    private IOnkostarApi onkostarApi;

    private OsTumorkonferenzToProtocolMapper mapper;

    @BeforeEach
    void setup(
            @Mock IOnkostarApi onkostarApi
    ) {
        this.onkostarApi = onkostarApi;
        this.mapper = new OsTumorkonferenzToProtocolMapper();
    }

    @Test
    void testShouldReturnMtbProtocolForDefaultImplementation() {
        var procedure = new Procedure(onkostarApi);
        procedure.setFormName("OS.Tumorkonferenz");
        procedure.setStartDate(Date.from(Instant.parse("2023-01-01T00:00:00Z")));
        procedure.setValue("Fragestellung", new Item("Fragestellung", "Test ok?"));
        procedure.setValue("Empfehlung", new Item("Empfehlung", "Rerun Test if not ok!"));

        var actual = mapper.apply(procedure);

        assertThat(actual)
                .isPresent()
                .contains("Fragestellung:\nTest ok?\n\nEmpfehlung:\nRerun Test if not ok!");
    }

}
