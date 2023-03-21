package DNPM.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;


/**
 * Mapper zum Ermitteln des Protokollauszugs für Formular "OS.Tumorkonferenz"
 *
 * @since 0.0.2
 */
public class OsTumorkonferenzToProtocolMapper implements ProcedureToProtocolMapper {
    @Override
    public Optional<String> apply(Procedure procedure) {
        assert(procedure.getFormName().equals("OS.Tumorkonferenz"));

        var fragestellung = procedure.getValue("Fragestellung");
        var empfehlung = procedure.getValue("Empfehlung");

        if (
                null != fragestellung && !fragestellung.getString().isBlank()
                && null != empfehlung && !empfehlung.getString().isBlank()
        ) {
            return Optional.of(String.format("Fragestellung:\n%s\n\nEmpfehlung:\n%s", fragestellung.getString(), empfehlung.getString()));
        } else if (null != fragestellung && !fragestellung.getString().isBlank()) {
            return Optional.of(fragestellung.getString());
        } else if (null != empfehlung && !empfehlung.getString().isBlank()) {
            return Optional.of(empfehlung.getString());
        }

        return Optional.empty();
    }
}
