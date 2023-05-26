package DNPM.services.molekulargenetik;

import DNPM.dto.Variant;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OsMolekulargenetikFormService implements MolekulargenetikFormService {

    /**
     * Ermittelt alle (unterstützten) Varianten zur Prozedur eines Formulars "OS.Molekulargenetik"
     * Unterstützte Varianten sind:
     * <uL>
     *     <li>Einfache Variante
     *     <li>CNV
     *     <li>Fusion
     * @param procedure Die Prozedur zum Formular "OS.Molekulargenetik"
     * @return Die unterstützten Varianten oder eine leere Liste, wenn keine Varianten gefunden wurden.
     */
    @Override
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
