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

package dev.dnpm.oshelper.services;

import dev.dnpm.oshelper.exceptions.FormException;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.constants.JaNeinUnbekannt;

import java.util.List;

public interface FormService {

    /**
     * Diese Methode übergibt die Prozedur-ID des zugehörigen Hauptformulars zu einem Unterformular
     * Siehe auch: <a href="https://github.com/CCC-MF/onkostar-plugin-forminfo/blob/master/src/main/java/de/ukw/ccc/onkostar/forminfo/services/FormInfoService.java">FormInfoService.java</a>
     *
     * @param procedureId Die Prozedur-ID des Unterformulars
     * @return Die Prozedur-ID des zugehörigen Hauptformulars
     * @throws FormException Wird geworfen, wenn ein Fehler auftrat
     */
    int getMainFormProcedureId(int procedureId) throws FormException;

    /**
     * Diese Methode übergibt die Prozedur-IDs von Unterformularen zu einem Formular
     * Siehe auch: <a href="https://github.com/CCC-MF/onkostar-plugin-forminfo/blob/master/src/main/java/de/ukw/ccc/onkostar/forminfo/services/FormInfoService.java">FormInfoService.java</a>
     *
     * @param procedureId Die Prozedur-ID des Formulars
     * @return Eine Liste mit Prozedur-IDs der Unterformulare
     */
    List<Integer> getSubFormProcedureIds(int procedureId);

    /**
     * Prüft, ob ein Formularfeld in der Prozedur einen Wert hat oder null ist
     * @param procedure Die zu prüfende Prozedur
     * @param fieldName Der Formularfeldname
     * @return Gibt <code>true</code> zurück, wenn das Feld einen Wert hat
     */
    static boolean hasValue(final Procedure procedure, final String fieldName) {
        return null != procedure.getValue(fieldName);
    }

    /**
     * Prüft, ob ein Formularfeld mit Ja/Nein/Unbekannt den Wert Ja hat
     * @param procedure Die zu prüfende Prozedur
     * @param fieldName Der Formularfeldname
     * @return Gibt <code>true</code> zurück, wenn das Feld den Wert "Ja" hat
     */
    static boolean isYes(final Procedure procedure, final String fieldName) {
        return hasValue(procedure, fieldName)
                && procedure.getValue(fieldName).getString().equals(JaNeinUnbekannt.JA.getCode());
    }

}
