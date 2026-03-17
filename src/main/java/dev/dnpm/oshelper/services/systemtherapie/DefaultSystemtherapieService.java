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

package dev.dnpm.oshelper.services.systemtherapie;

import dev.dnpm.oshelper.dto.EcogStatusWithDate;
import dev.dnpm.oshelper.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Standardimplementierung des Systemtherapieservices
 *
 * @since 0.2.0
 */
public class DefaultSystemtherapieService implements SystemtherapieService {

    private static final String ECOG_FIELD = "ECOGvorTherapie";

    private final IOnkostarApi onkostarApi;

    private final SettingsService settingsService;

    public DefaultSystemtherapieService(final IOnkostarApi onkostarApi, final SettingsService settingsService) {
        this.onkostarApi = onkostarApi;
        this.settingsService = settingsService;
    }

    /**
     * Ermittelt eine Zusammenfassung der systemischen Therapien für eine Erkrankung
     *
     * @param diseaseId Die ID der Erkrankung
     * @return Zusammenfassung der systemischen Therapien
     */
    @Override
    public List<Map<String, String>> getSystemischeTherapienFromDiagnose(int diseaseId) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Procedure prozedur : onkostarApi.getProceduresForDiseaseByForm(diseaseId, getFormName())) {
            prozedurToProzedurwerteMapper(prozedur).apply(prozedur).ifPresent(result::add);
        }
        return result;
    }

    /**
     * Übergibt aktuell immer den Mapper für das Formular "OS.Systemische Therapie",
     * da beide bekannte Varianten damit gemappt werden können.
     *
     * @param procedure Die Prozedur für die ein Mapper erstellt werden soll
     * @return Der Mapper für die Prozedur
     */
    @Override
    public ProzedurToProzedurwerteMapper prozedurToProzedurwerteMapper(Procedure procedure) {
        return new OsSystemischeTherapieToProzedurwerteMapper();
    }

    /**
     * Ermittelt den letzten bekannten ECOG-Status aus allen Systemtherapieformularen des Patienten
     *
     * @param patient Der zu verwendende Patient
     * @return Der ECOG-Status als String oder leeres Optional
     */
    @Override
    public Optional<String> latestEcogStatus(Patient patient) {
        return ecogStatus(patient).stream()
                .max(Comparator.comparing(EcogStatusWithDate::getDate))
                .map(EcogStatusWithDate::getStatus);
    }

    /**
     * Ermittelt jeden bekannten ECOG-Status aus allen Systemtherapieformularen des Patienten
     *
     * @param patient Der zu verwendende Patient
     * @return Eine Liste mit Datum und ECOG-Status als String
     */
    @Override
    public List<EcogStatusWithDate> ecogStatus(Patient patient) {
        return patient.getDiseases().stream()
                .flatMap(disease -> onkostarApi.getProceduresForDiseaseByForm(disease.getId(), getFormName()).stream())
                .filter(procedure -> null != procedure.getStartDate())
                .sorted(Comparator.comparing(Procedure::getStartDate))
                .map(procedure -> {
                    try {
                        return new EcogStatusWithDate(procedure.getStartDate(), procedure.getValue(ECOG_FIELD).getString());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getFormName() {
        return settingsService
                .getSetting("systemtherapieform")
                .orElse("OS.Systemische Therapie");
    }
}
