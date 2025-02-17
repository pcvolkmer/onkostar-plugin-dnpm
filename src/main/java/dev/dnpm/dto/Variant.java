/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.dnpm.dto;

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
