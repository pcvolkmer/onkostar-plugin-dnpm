package DNPM.services.mtb;

import de.itc.onkostar.api.Procedure;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OsTumorkonferenzVarianteUkwToProtocolMapper implements ProcedureToProtocolMapper {
    @Override
    public Optional<String> apply(List<Procedure> procedures) {
        procedures.forEach(procedure -> {
            assert (procedure.getFormName().equals("OS.Tumorkonferenz.VarianteUKW"));
        });

        procedures.sort(Comparator.comparing(Procedure::getStartDate));

        var result = procedures.stream().map(procedure -> {
            var fragestellung = procedure.getValue("Fragestellung");
            var empfehlung = procedure.getValue("Empfehlung");

            if (
                    null != fragestellung && !fragestellung.getString().isBlank()
                            && null != empfehlung && !empfehlung.getString().isBlank()
            ) {
                return String.format("Fragestellung:\n%s\n\nEmpfehlung:\n%s", fragestellung.getString().trim(), empfehlung.getString().trim());
            } else if (null != fragestellung && !fragestellung.getString().isBlank()) {
                return fragestellung.getString().trim();
            } else if (null != empfehlung && !empfehlung.getString().isBlank()) {
                return empfehlung.getString().trim();
            }
            return "";
        }).collect(Collectors.joining("\n\n"));

        if (!result.isBlank()) {
            return Optional.of(result);
        }

        return Optional.empty();
    }
}
