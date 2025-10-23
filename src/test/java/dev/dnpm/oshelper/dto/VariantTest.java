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
