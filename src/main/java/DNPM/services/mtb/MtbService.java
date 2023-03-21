package DNPM.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;

public interface MtbService {
    String getProtocol(List<Procedure> procedures);

    static ProcedureToProtocolMapper procedureToProtocolMapper(Procedure procedure) {
        switch (procedure.getFormName()) {
            case "OS.Tumorkonferenz":
                return new OsTumorkonferenzToProtocolMapper();
            case "OS.Tumorkonferenz.VarianteUKW":
                return new OsTumorkonferenzVarianteUkwToProtocolMapper();
            default:
                return p -> Optional.empty();
        }
    }
}
