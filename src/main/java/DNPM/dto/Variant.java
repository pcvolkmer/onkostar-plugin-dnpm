package DNPM.dto;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;

public class Variant {
    private final Integer id;

    private final String shortDescription;

    private Variant(
            final int id,
            final String shortDescription
    ) {
        this.id = id;
        this.shortDescription = shortDescription.trim();
    }

    public Integer getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public static Optional<Variant> fromProcedure(Procedure procedure) {
        if (! "OS.Molekulargenetische Untersuchung".equals(procedure.getFormName())) {
            return Optional.empty();
        }

        var ergebnis = procedure.getValue("Ergebnis");
        var gene = procedure.getValue("Untersucht");
        var exon = procedure.getValue("ExonInt");
        var pathogenitaetsklasse = procedure.getValue("Pathogenitaetsklasse");

        if (null == gene) {
            return Optional.empty();
        }

        if (ergebnis.getString().equals("P")) {
            return Optional.of(
                    new Variant(
                            procedure.getId(),
                            String.format("Einfache Variante: %s, %s, %s", gene.getString(), exon.getString(), pathogenitaetsklasse.getString())
                    )
            );
        } else if (ergebnis.getString().equals("CNV")) {
            return Optional.of(
                    new Variant(
                            procedure.getId(),
                            String.format("Copy Number Variation: %s, %s, %s", gene.getString(), exon.getString(), pathogenitaetsklasse.getString())
                    )
            );
        } else if (ergebnis.getString().equals("F")) {
            return Optional.of(
                    new Variant(
                            procedure.getId(),
                            String.format("Fusion: %s, %s, %s", gene.getString(), exon.getString(), pathogenitaetsklasse.getString())
                    )
            );
        } else {
            return Optional.empty();
        }
    }
}
