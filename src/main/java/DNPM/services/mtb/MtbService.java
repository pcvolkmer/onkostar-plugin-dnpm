package DNPM.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.List;

public interface MtbService {
    String getProtocol(List<Procedure> procedures);

    ProcedureToProtocolMapper procedureToProtocolMapper(Procedure procedure);
}
