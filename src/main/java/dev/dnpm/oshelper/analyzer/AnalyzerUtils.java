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

package dev.dnpm.oshelper.analyzer;

import java.util.Map;
import java.util.Optional;

/**
 * Klasse mit Hilfsfunktionen für Analyzer
 *
 * @since 0.1.0
 */
public class AnalyzerUtils {

    private AnalyzerUtils() {}

    /**
     * Prüft, ob in InputMap einen Eintrag mit key <code>key</code> und Typ <code>type</code>
     * gefunden wurde.
     *
     * @param input InputMap
     * @param key Key des Werts
     * @param type Typ des Werts
     * @return <code>true</code>>, wenn ein Wert von dem Typ gefunden wurde
     */
    public static boolean requiredValuePresent(final Map<String, Object> input, final String key, final Class<?> type) {
        var value = input.get(key);

        if (null == value) {
            return false;
        }

        return type.isInstance(value);
    }

    /**
     * Übergibt ein Optional mit Wert, wenn in InputMap ein Eintrag mit key <code>key</code> und Typ <code>type</code>
     * gefunden wurde. Anderenfalls ein leeres Optional
     *
     * <p><b>Beispiel</b>
     * <pre>
     *     var id = AnalyzerUtils.getRequiredValue(input, "id", Integer.class);
     *     if (id.isEmpty()) {
     *         logger.error("Keine ID angegeben!");
     *         return false;
     *     }
     *
     *     var idNummer = id.get();
     *     ...
     * </pre>
     *
     * @param input InputMap
     * @param key Key des Werts
     * @param type Typ des Werts
     * @return Optional mit entsprechendem Wert oder leeres Optional
     */
    public static <T> Optional<T> getRequiredValue(final Map<String, Object> input, final String key, final Class<T> type) {
        if (! requiredValuePresent(input, key, type)) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        var result = Optional.of((T)input.get(key));

        return result;
    }

    /**
     * Prüft, ob ein Wert in der InputMap als Zeichenkette dem angegebenen RegExp entspricht
     *
     * @param input InputMap
     * @param key Key des Werts
     * @param regexp Der zu prüfende reguläre Ausdruck
     * @return <code>true</code>>, wenn ein Wert gefunden wurde, der dem RegExp entspricht
     */
    public static boolean requiredValueMatches(final Map<String, Object> input, final String key, final String regexp) {
        var value = input.get(key);

        if (null == value) {
            return false;
        }

        return value.toString().matches(regexp);
    }

    /**
     * Übergibt ein Optional mit dem Wert als Zeichenkette, wenn er dem angegebenen RegExp entspricht.
     * Hierzu wird die Methode <code>toString()</code> auf den Wert angewendet.
     *
     * @param input InputMap
     * @param key Key des Werts
     * @param regexp Der zu prüfende reguläre Ausdruck
     * @return Optional mit entsprechendem Wert als Zeichenkette oder leeres Optional
     */
    public static Optional<String> getRequiredValueMatching(final Map<String, Object> input, final String key, final String regexp) {
        if (! requiredValueMatches(input, key, regexp)) {
            return Optional.empty();
        }

        return Optional.of(input.get(key).toString());
    }

    /**
     * Prüft, ob ein Wert in der InputMap eine ID ist und damit eine Zahl größer Null ist.
     *
     * @param input InputMap
     * @param key Key des Werts
     * @return <code>true</code>>, wenn ein Wert gefunden wurde, der dem RegExp entspricht
     */
    public static boolean requiredValueIsId(final Map<String, Object> input, final String key) {
        return requiredValuePresent(input, key, Integer.class) && Integer.parseInt(input.get(key).toString()) > 0;
    }

    /**
     * Übergibt ein Optional, wenn der Wert eine ID ist und damit eine Zahl größer Null ist.
     * <p><b>Beispiel</b>
     * <pre>
     *     var id = AnalyzerUtils.getRequiredId(input, "id");
     *     if (id.isEmpty()) {
     *         logger.error("Keine gültige ID angegeben!");
     *         return false;
     *     }
     *
     *     // Ist hier immer größer als Null
     *     var idNummer = id.get();
     *     ...
     * </pre>
     *
     * @param input InputMap
     * @param key Key des Werts
     * @return Optional mit entsprechendem Wert oder leeres Optional
     */
    public static Optional<Integer> getRequiredId(final Map<String, Object> input, final String key) {
        if (! requiredValuePresent(input, key, Integer.class)) {
            return Optional.empty();
        }

        var id = (int)input.get(key);

        if (id > 0) {
            return Optional.of(id);
        }

        return Optional.empty();
    }

}
