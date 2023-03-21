package DNPM.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Standardimplementierung des MtbService
 *
 * @since 0.0.2
 */
public class DefaultMtbService implements MtbService {

    private final IOnkostarApi onkostarApi;

    public DefaultMtbService(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    @Override
    public String getProtocol(List<Procedure> procedures) {
        return procedures.stream()
                .sorted(Comparator.comparing(Procedure::getStartDate))
                .map(procedure -> {
                    var mapper = procedureToProtocolMapper(procedure);
                    return mapper.apply(procedure);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.joining("\n\n"));

    }

    @Override
    public ProcedureToProtocolMapper procedureToProtocolMapper(Procedure procedure) {
        switch (procedure.getFormName()) {
            case "OS.Tumorkonferenz":
                return new OsTumorkonferenzToProtocolMapper();
            case "OS.Tumorkonferenz.VarianteUKW":
                return new OsTumorkonferenzVarianteUkwToProtocolMapper();
            case "MR.MTB_Anmeldung":
                return new MrMtbAnmeldungToProtocolMapper(this.onkostarApi);
            default:
                return p -> Optional.empty();
        }
    }

}
