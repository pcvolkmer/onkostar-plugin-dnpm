package dev.dnpm.services.systemtherapie;

import de.itc.onkostar.api.Procedure;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Mapper um ein Systemtherapieformular in eine Optional-Map mit Prozedurwerten umzuwandeln
 *
 * @since 0.2.0
 */
public interface ProzedurToProzedurwerteMapper extends Function<Procedure, Optional<Map<String, String>>> {}
