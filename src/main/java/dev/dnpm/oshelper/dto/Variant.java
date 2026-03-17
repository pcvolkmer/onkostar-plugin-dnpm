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

package dev.dnpm.oshelper.dto;

import de.itc.onkostar.api.Procedure;

import java.util.Optional;

/**
 * Ein Auszug der Variante aus dem NGS-Bericht zur Übertragung an das Frontend zur Auswahl der stützenden molekularen Alteration
 *
 * @since 0.2.0
 */
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

    /**
     * Erstellt ein Optional einer Variante aus einer Prozedur
     * @param procedure Die zu verwendende Prozedur
     * @return Das Optional, wenn die Prozedur verwendet werden kann, ansonsten ein leeres Optional
     */
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
