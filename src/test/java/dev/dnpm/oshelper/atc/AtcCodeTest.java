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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtcCodeTest {

    @Test
    void testShouldDetectAtcCodeScheme() {
        assertTrue(AtcCode.isAtcCode("L01"));
        assertTrue(AtcCode.isAtcCode("L01A"));
        assertTrue(AtcCode.isAtcCode("L01AA"));
        assertTrue(AtcCode.isAtcCode("L01AA01"));
    }

    @Test
    void testShouldDetectInvalidAtcCodeScheme() {
        assertFalse(AtcCode.isAtcCode(null));
        assertFalse(AtcCode.isAtcCode("  "));
        assertFalse(AtcCode.isAtcCode("irgendwas"));
        assertFalse(AtcCode.isAtcCode("L00AA"));
        assertFalse(AtcCode.isAtcCode("Z01AA"));
        assertFalse(AtcCode.isAtcCode("L01AA0"));
    }

}
