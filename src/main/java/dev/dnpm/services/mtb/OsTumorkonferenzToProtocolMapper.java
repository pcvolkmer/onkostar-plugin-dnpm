package dev.dnpm.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;


/**
 * Mapper zum Ermitteln des Protokollauszugs für Formular "OS.Tumorkonferenz"
 *
 * @since 0.0.2
 */
public class OsTumorkonferenzToProtocolMapper implements ProcedureToProtocolMapper {

    /**
     * Wandelt eine Prozedur mit Formularnamen "OS.Tumorkonferenz" in ein {@link Optional} mit einer
     * Zeichenkette oder im Fehlerfall in ein leeres Optional um.
     * @param procedure Die Prozedur, für die eine Zusammenfassung ermittelt werden soll.
     * @return Das {@link Optional} mit, im Erfolgsfall, der Zusammenfassung für die Prozedur.
     */
    @Override
    public Optional<String> apply(Procedure procedure) {
        if ((!procedure.getFormName().equals("OS.Tumorkonferenz"))) {
            throw new AssertionError("Procedure is not of form type 'OS.Tumorkonferenz'");
        }

        var fragestellung = procedure.getValue("Fragestellung");
        var empfehlung = procedure.getValue("Empfehlung");

        if (
                null != fragestellung && !fragestellung.getString().isBlank()
                && null != empfehlung && !empfehlung.getString().isBlank()
        ) {
            return Optional.of(String.format("Fragestellung:%n%s%n%nEmpfehlung:%n%s", fragestellung.getString(), empfehlung.getString()));
        } else if (null != fragestellung && !fragestellung.getString().isBlank()) {
            return Optional.of(fragestellung.getString());
        } else if (null != empfehlung && !empfehlung.getString().isBlank()) {
            return Optional.of(empfehlung.getString());
        }

        return Optional.empty();
    }
}
