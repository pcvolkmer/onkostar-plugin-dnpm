package DNPM.services.molekulargenetik;

import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OsMolekluargenetikFormServiceTest {

    private OsMolekulargenetikFormService service;

    @BeforeEach
    void setup() {
        this.service = new OsMolekulargenetikFormService();
    }

    @Test
    void testShouldReturnVariants() {

        var procedure = new Procedure(null);
        procedure.setId(123);
        procedure.setFormName("OS.Molekulargenetik");

        var subProcedure1 = new Procedure(null);
        subProcedure1.setId(1123);
        subProcedure1.setFormName("OS.Molekulargenetische Untersuchung");
        subProcedure1.setValue("Ergebnis", new Item("Ergebnis", "P"));
        subProcedure1.setValue("Untersucht", new Item("Untersucht", "BRAF"));
        subProcedure1.setValue("ExonInt", new Item("ExonInt", 123));
        subProcedure1.setValue("Pathogenitaetsklasse", new Item("Pathogenitaetsklasse", "2"));
        procedure.addSubProcedure("MolekulargenetischeUntersuchung", subProcedure1);

        var subProcedure2 = new Procedure(null);
        subProcedure2.setId(2123);
        subProcedure2.setFormName("OS.Molekulargenetische Untersuchung");
        subProcedure2.setValue("Ergebnis", new Item("Ergebnis", "CNV"));
        subProcedure2.setValue("Untersucht", new Item("Untersucht", "BRAF"));
        subProcedure2.setValue("ExonInt", new Item("ExonInt", 123));
        subProcedure2.setValue("Pathogenitaetsklasse", new Item("Pathogenitaetsklasse", "2"));
        procedure.addSubProcedure("MolekulargenetischeUntersuchung", subProcedure2);

        var actual = service.getVariants(procedure);

        assertThat(actual).hasSize(2);
    }

}
