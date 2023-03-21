package DNPM.services.mtb;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class MrMtbAnmeldungToProtocolMapper implements ProcedureToProtocolMapper {

    private final IOnkostarApi onkostarApi;

    public MrMtbAnmeldungToProtocolMapper(final IOnkostarApi onkostarApi) {
        this.onkostarApi = onkostarApi;
    }

    @Override
    public Optional<String> apply(Procedure procedure) {
        assert (procedure.getFormName().equals("MR.MTB_Anmeldung"));

        var resultParts = new ArrayList<String>();

        var fragestellung = procedure.getValue("Fragestellung");
        if (null != fragestellung && !fragestellung.getString().isBlank()) {
            resultParts.add(String.format("Fragestellung:\n%s", fragestellung.getString()));
        }

        var refEmpfehlung = procedure.getValue("Empfehlung");
        if (null != refEmpfehlung && refEmpfehlung.getInt() > 0) {
            var empfehlungsProzedur = onkostarApi.getProcedure(refEmpfehlung.getInt());
            var refEinzelempfehlungen = onkostarApi.getSubprocedures(empfehlungsProzedur.getId());

            if (null != refEinzelempfehlungen) {
                refEinzelempfehlungen.stream()
                        .sorted(Comparator.comparingInt(proc -> proc.getValue("Prioritaet").getInt()))
                        .forEach(proc -> {
                            if (proc.getFormName().equals("MR.MTB_Einzelempfehlung")) {
                                var empfehlung = proc.getValue("Empfehlung");
                                if (null != empfehlung && !empfehlung.getString().isBlank()) {
                                    resultParts.add(String.format("Empfehlung:\n%s", empfehlung.getString()));
                                }
                            }
                        });
            }
        }

        if (resultParts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(String.join("\n\n", resultParts));
    }
}
