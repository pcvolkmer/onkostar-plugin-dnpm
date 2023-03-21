package DNPM.services.mtb;

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

    @Override
    public String getProtocol(List<Procedure> procedures) {
        return procedures.stream()
                .sorted(Comparator.comparing(Procedure::getStartDate))
                .map(procedure -> {
                    var mapper = MtbService.procedureToProtocolMapper(procedure);
                    return mapper.apply(procedure);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.joining("\n\n"));

    }

}
