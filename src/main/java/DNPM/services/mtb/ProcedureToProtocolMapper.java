package DNPM.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface ProcedureToProtocolMapper extends Function<List<Procedure>, Optional<String>> {}
