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

package dev.dnpm.oshelper.services.systemtherapie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import dev.dnpm.oshelper.atc.AtcCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementierung zum Mappen des Formulars "OS.Systemische Therapie" auf die Prozedurwerte
 *
 * @since 0.2.0
 */
public class OsSystemischeTherapieToProzedurwerteMapper implements ProzedurToProzedurwerteMapper {

    private static final Logger logger = LoggerFactory.getLogger(OsSystemischeTherapieToProzedurwerteMapper.class);

    @Override
    public Optional<Map<String, String>> apply(Procedure procedure) {
        try {
            return Optional.of(getProzedurwerte(procedure));
        } catch (Exception e) {
            logger.error("Fehler beim Mappen der Prozedur auf Prozedurwerte", e);
            return Optional.empty();
        }
    }

    private static Map<String, String> getProzedurwerte(Procedure prozedur) {
        List<String> wirkstoffListe = new ArrayList<>();
        // SubstanzenCodesListe enthält die Liste der SubstanzenCodes
        List<Map<String, String>> substanzenCodesListe = new ArrayList<>();

        // alle Werte der Prozedur auslesen
        Map<String, Item> alleWerte = prozedur.getAllValues();
        // Prozedurwerte enthält nur die interessanten Werte
        Map<String, String> prozedurwerte = new HashMap<>();
        // alle Werte durchgehen und die interessanten übernehmen
        if (alleWerte.containsKey("Beendigung")) {
            prozedurwerte.put("Beendigung", alleWerte.get("Beendigung").getValue());
        }
        if (alleWerte.containsKey("Ergebnis")) {
            prozedurwerte.put("Ergebnis", alleWerte.get("Ergebnis").getValue());
        }
        if (alleWerte.containsKey("Beginn")) {
            prozedurwerte.put("Beginn", alleWerte.get("Beginn").getString());
        }
        if (alleWerte.containsKey("Ende")) {
            prozedurwerte.put("Ende", alleWerte.get("Ende").getString());
        }
        if (alleWerte.containsKey("SubstanzenList")) {
            List<Map<String, String>> substanzList = alleWerte.get("SubstanzenList").getValue();
            for (var substanz : substanzList) {
                var substanzCodes = getSubstanzCode(substanz);
                substanzenCodesListe.add(substanzCodes);
                wirkstoffListe.add(substanzCodes.get("substance"));
            }
        }

        prozedurwerte.put("Wirkstoffe", String.join(", ", wirkstoffListe));
        try {
            ObjectMapper mapper = new ObjectMapper();
            prozedurwerte.put("WirkstoffCodes", mapper.writeValueAsString(substanzenCodesListe));
        } catch (JsonProcessingException e) {
            logger.error("Kann 'WirkstoffCodes' nicht in JSON-String mappen", e);
        }

        return prozedurwerte;
    }

    private static Map<String, String> getSubstanzCode(Map<String, String> substanz) {
        Map<String, String> substanzCode = new HashMap<>();
        if (substanz.containsKey("Substanz")) {
            if (AtcCode.isAtcCode(substanz.get("Substanz"))) {
                substanzCode.put("system", "ATC");
            } else {
                substanzCode.put("system", "other");
            }
            substanzCode.put("code", substanz.get("Substanz"));

        }
        if (substanz.containsKey("Substanz_shortDescription")) {
            substanzCode.put("substance", substanz.get("Substanz_shortDescription"));
        }
        return substanzCode;
    }
}
