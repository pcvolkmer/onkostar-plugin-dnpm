package DNPM.services.molekulargenetik;

import DNPM.dto.Variant;
import DNPM.security.PersonPoolSecured;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OsMolekulargenetikFormService implements MolekulargenetikFormService {

    @Override
    @PersonPoolSecured
    public List<Variant> getVariants(Procedure procedure) {
        if (! "OS.Molekulargenetik".equals(procedure.getFormName())) {
            return List.of();
        }

        var subforms = procedure.getSubProceduresMap().get("MolekulargenetischeUntersuchung");
        if (null == subforms) {
            return List.of();
        }

        return subforms.stream()
                .map(Variant::fromProcedure)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }
}
