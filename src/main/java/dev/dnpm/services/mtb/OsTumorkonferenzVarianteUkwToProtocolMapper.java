package dev.dnpm.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;

/**
 * Mapper zum Ermitteln des Protokollauszugs für Formular "OS.Tumorkonferenz.VarianteUKW"
 *
 * @since 0.0.2
 */
public class OsTumorkonferenzVarianteUkwToProtocolMapper implements ProcedureToProtocolMapper {

    /**
     * Wandelt eine Prozedur mit Formularnamen "OS.Tumorkonferenz.VarianteUKW" in ein {@link Optional} mit einer
     * Zeichenkette oder im Fehlerfall in ein leeres Optional um.
     * @param procedure Die Prozedur, für die eine Zusammenfassung ermittelt werden soll.
     * @return Das {@link Optional} mit, im Erfolgsfall, der Zusammenfassung für die Prozedur.
     */
    @Override
    public Optional<String> apply(Procedure procedure) {
        if ((!procedure.getFormName().equals("OS.Tumorkonferenz.VarianteUKW"))) {
            throw new AssertionError("Procedure is not of form type 'OS.Tumorkonferenz.VarianteUKW'");
        }


        var fragestellung = procedure.getValue("Fragestellung");
        var empfehlung = procedure.getValue("Empfehlung");

        if (
                null != fragestellung && !fragestellung.getString().isBlank()
                        && null != empfehlung && !empfehlung.getString().isBlank()
        ) {
            return Optional.of(String.format("Fragestellung:%n%s%n%nEmpfehlung:%n%s", fragestellung.getString().trim(), empfehlung.getString().trim()));
        } else if (null != fragestellung && !fragestellung.getString().isBlank()) {
            return Optional.of(fragestellung.getString().trim());
        } else if (null != empfehlung && !empfehlung.getString().isBlank()) {
            return Optional.of(empfehlung.getString().trim());
        }
        return Optional.empty();
    }
}
