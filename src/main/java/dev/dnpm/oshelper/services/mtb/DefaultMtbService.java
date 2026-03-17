/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (C) 2023-2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.oshelper.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Standardimplementierung des MtbService
 *
 * @since 0.0.2
 */
@Deprecated(forRemoval = true, since = "2.1.0")
public class DefaultMtbService implements MtbService {

    private final IOnkostarApi onkostarApi;

    public DefaultMtbService(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    /**
     * Zusammenfassung der Prozeduren.
     * Dabei werden alle Prozeduren sortiert, mit ermitteltem Mapper in {@link Optional} eines {@link String}s
     * gewandelt und, wenn dies erfolgreich war, die Zeichenkette extrahiert.
     * Im Anschluss wird die Abfolge der Zeichenketten mit den einzelnen Prozedur-Zusammenfassungen in eine
     * einzige Zusammenfassung zusammengefügt.
     * @param procedures Prozeduren, die zusammen gefasst werden sollen
     * @return Text mit Zusammenfassung aller übergebenen Prozeduren
     */
    @Override
    public String getProtocol(List<Procedure> procedures) {
        return this.sortedDistinctProcedureProtocolList(procedures.stream())
                .collect(Collectors.joining("\n\n"));
    }

    private Stream<String> sortedDistinctProcedureProtocolList(Stream<Procedure> procedures) {
        return procedures
                .sorted(Comparator.comparing(Procedure::getStartDate))
                .map(this::selectAndApplyMapper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct();
    }

    /**
     * Übergibt anzuwendenden Mapper für eine Prozedur.
     * Wurde keine Implementierung festgelegt, wird ein Mapper zurückgegeben, der eine
     * Prozedur in ein leeres {@link Optional} zurück gibt, übergeben.
     * @param procedure Prozedur, für die ein Mapper ermittelt werden soll
     * @return Mapper für diese Prozedur
     */
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
