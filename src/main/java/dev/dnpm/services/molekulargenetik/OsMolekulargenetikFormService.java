package dev.dnpm.services.molekulargenetik;

import dev.dnpm.dto.Variant;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OsMolekulargenetikFormService implements MolekulargenetikFormService {

    /**
     * Ermittelt alle (unterstützten) Varianten zur Prozedur eines Formulars "OS.Molekulargenetik" (oder Variante)
     * Unterstützte Varianten sind:
     * <uL>
     *     <li>Einfache Variante
     *     <li>CNV
     *     <li>Fusion
     * @param procedure Die Prozedur zum Formular "OS.Molekulargenetik" (oder Variante)
     * @return Die unterstützten Varianten oder eine leere Liste, wenn keine Varianten gefunden wurden.
     */
    @Override
    public List<Variant> getVariants(Procedure procedure) {
        if (! procedureWithUsableFormVariant(procedure)) {
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

    private boolean procedureWithUsableFormVariant(Procedure procedure) {
        return "OS.Molekulargenetik".equals(procedure.getFormName())
                || "UKER.Molekulargenetik".equals(procedure.getFormName());
    }
}
