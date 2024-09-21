package dev.dnpm.services.molekulargenetik;

import dev.dnpm.dto.Variant;
import de.itc.onkostar.api.Procedure;

import java.util.List;

/**
 * Schnittstellenbeschreibung für Methoden zum Formular "OS.Molekulargenetik"
 */
public interface MolekulargenetikFormService {

    /**
     * Ermittelt alle (unterstützten) Varianten zur Prozedur eines Formulars "OS.Molekulargenetik"
     * @param procedure Die Prozedur zum Formular "OS.Molekulargenetik"
     * @return Die unterstützten Varianten oder eine leere Liste, wenn keine Varianten gefunden wurden.
     */
    List<Variant> getVariants(Procedure procedure);

}
