package dev.dnpm.services.mtb;

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
