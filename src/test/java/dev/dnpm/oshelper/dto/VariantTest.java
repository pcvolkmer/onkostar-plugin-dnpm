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

package dev.dnpm.oshelper.dto;

import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VariantTest {

    @Test
    void testShouldMapVariantFromProcedureForSimpleVariant() {
        var procedure = new Procedure(null);
        procedure.setId(12345);
        procedure.setFormName("OS.Molekulargenetische Untersuchung");

        procedure.setValue("Ergebnis", new Item("Ergebnis", "P"));
        procedure.setValue("Untersucht", new Item("Untersucht", "BRAF"));
        procedure.setValue("ExonInt", new Item("ExonInt", 123));
        procedure.setValue("Pathogenitaetsklasse", new Item("Pathogenitaetsklasse", "2"));

        var actual = Variant.fromProcedure(procedure);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(12345);
        assertThat(actual.get().getErgebnis()).isEqualTo("Einfache Variante (Mutation)");
        assertThat(actual.get().getGen()).isEqualTo("BRAF");
        assertThat(actual.get().getExon()).isEqualTo("123");
        assertThat(actual.get().getPathogenitaetsklasse()).isEqualTo("2");
    }

    @Test
    void testShouldMapVariantFromProcedureForCopyNumberVariation() {
        var procedure = new Procedure(null);
        procedure.setId(12345);
        procedure.setFormName("OS.Molekulargenetische Untersuchung");

        procedure.setValue("Ergebnis", new Item("Ergebnis", "CNV"));
        procedure.setValue("Untersucht", new Item("Untersucht", "BRAF"));
        procedure.setValue("ExonInt", new Item("ExonInt", 123));
        procedure.setValue("Pathogenitaetsklasse", new Item("Pathogenitaetsklasse", "2"));

        var actual = Variant.fromProcedure(procedure);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(12345);
        assertThat(actual.get().getErgebnis()).isEqualTo("Copy Number Variation (CNV)");
        assertThat(actual.get().getGen()).isEqualTo("BRAF");
        assertThat(actual.get().getExon()).isEqualTo("123");
        assertThat(actual.get().getPathogenitaetsklasse()).isEqualTo("2");
    }

    @Test
    void testShouldMapVariantFromProcedureForFusion() {
        var procedure = new Procedure(null);
        procedure.setId(12345);
        procedure.setFormName("OS.Molekulargenetische Untersuchung");

        procedure.setValue("Ergebnis", new Item("Ergebnis", "F"));
        procedure.setValue("Untersucht", new Item("Untersucht", "BRAF"));
        procedure.setValue("ExonInt", new Item("ExonInt", 123));
        procedure.setValue("Pathogenitaetsklasse", new Item("Pathogenitaetsklasse", "2"));

        var actual = Variant.fromProcedure(procedure);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(12345);
        assertThat(actual.get().getErgebnis()).isEqualTo("Fusion (Translokation Inversion Insertion)");
        assertThat(actual.get().getGen()).isEqualTo("BRAF");
        assertThat(actual.get().getExon()).isEqualTo("123");
        assertThat(actual.get().getPathogenitaetsklasse()).isEqualTo("2");
    }

    @Test
    void testShouldNotMapVariantFromProcedureForUnknownVariant() {
        var procedure = new Procedure(null);
        procedure.setId(12345);
        procedure.setFormName("OS.Molekulargenetische Untersuchung");

        procedure.setValue("Ergebnis", new Item("Ergebnis", "X"));
        procedure.setValue("Untersucht", new Item("Untersucht", "BRAF"));
        procedure.setValue("ExonInt", new Item("ExonInt", 123));
        procedure.setValue("Pathogenitaetsklasse", new Item("Pathogenitaetsklasse", "2"));

        var actual = Variant.fromProcedure(procedure);

        assertThat(actual).isEmpty();
    }

    @Test
    void testShouldNotMapVariantFromUnknownProcedureForm() {
        var procedure = new Procedure(null);
        procedure.setId(12345);
        procedure.setFormName("ABC.Irgendwas");

        procedure.setValue("Testfeld", new Item("Testfeld", "T"));

        var actual = Variant.fromProcedure(procedure);

        assertThat(actual).isEmpty();
    }

}
