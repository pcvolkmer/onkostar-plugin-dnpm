package dev.dnpm.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;

public interface MtbService {
    /**
     * Zusammenfassung der Prozeduren
     * @param procedures Prozeduren, die zusammen gefasst werden sollen
     * @return Text mit Zusammenfassung der Prozeduren
     */
    String getProtocol(List<Procedure> procedures);

    /**
     * Übergibt anzuwendenden Mapper für eine Prozedur
     * @param procedure Prozedur, für die ein Mapper ermittelt werden soll
     * @return Mapper für diese Prozedur
     */
    ProcedureToProtocolMapper procedureToProtocolMapper(Procedure procedure);

    /**
     * Select mapper using method {@link #procedureToProtocolMapper(Procedure)} and apply procedure
     * @param procedure The Procedure to select mapper for and apply
     * @return {@link Optional} with protocol or empty {@link Optional}
     */
    default Optional<String> selectAndApplyMapper(Procedure procedure) {
        return this.procedureToProtocolMapper(procedure).apply(procedure);
    }
}
