package DNPM.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.List;

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
}
