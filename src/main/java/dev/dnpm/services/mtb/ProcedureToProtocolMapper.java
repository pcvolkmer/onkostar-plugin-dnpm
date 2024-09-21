package dev.dnpm.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface ProcedureToProtocolMapper extends Function<Procedure, Optional<String>> {}
