package DNPM.dto;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;

public class Variant {
    private final Integer id;

    private final String ergebnis;

    private final String gen;

    private final String exon;

    private final String pathogenitaetsklasse;

    private Variant(
            final int id,
            final String ergebnis,
            final String gen,
            final String exon,
            final String pathogenitaetsklasse
    ) {
        this.id = id;
        this.ergebnis = ergebnis;
        this.gen = gen;
        this.exon = exon;
        this.pathogenitaetsklasse = pathogenitaetsklasse;
    }

    public Integer getId() {
        return id;
    }

    public String getErgebnis() {
        return ergebnis;
    }

    public String getGen() {
        return gen;
    }

    public String getExon() {
        return exon;
    }

    public String getPathogenitaetsklasse() {
        return pathogenitaetsklasse;
    }

    public static Optional<Variant> fromProcedure(Procedure procedure) {
        if (!"OS.Molekulargenetische Untersuchung".equals(procedure.getFormName())) {
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
                            "Einfache Variante (Mutation)",
                            gene.getString().isBlank() ? "-" : gene.getString(),
                            null == exon || exon.getString().isBlank() ? "-" : exon.getString(),
                            null == pathogenitaetsklasse || pathogenitaetsklasse.getString().isBlank() ? "-" : pathogenitaetsklasse.getString()
                    )
            );
        } else if (ergebnis.getString().equals("CNV")) {
            return Optional.of(
                    new Variant(
                            procedure.getId(),
                            "Copy Number Variation (CNV)",
                            gene.getString().isBlank() ? "-" : gene.getString(),
                            null == exon || exon.getString().isBlank() ? "-" : exon.getString(),
                            null == pathogenitaetsklasse || pathogenitaetsklasse.getString().isBlank() ? "-" : pathogenitaetsklasse.getString()
                    )
            );
        } else if (ergebnis.getString().equals("F")) {
            return Optional.of(
                    new Variant(
                            procedure.getId(),
                            "Fusion (Translokation Inversion Insertion)",
                            gene.getString().isBlank() ? "-" : gene.getString(),
                            null == exon || exon.getString().isBlank() ? "-" : exon.getString(),
                            null == pathogenitaetsklasse || pathogenitaetsklasse.getString().isBlank() ? "-" : pathogenitaetsklasse.getString()
                    )
            );
        } else {
            return Optional.empty();
        }
    }
}
