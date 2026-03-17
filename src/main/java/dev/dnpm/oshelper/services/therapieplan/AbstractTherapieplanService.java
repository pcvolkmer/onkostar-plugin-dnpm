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

package dev.dnpm.oshelper.services.therapieplan;

import dev.dnpm.oshelper.services.FormService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTherapieplanService implements TherapieplanService {

    protected final IOnkostarApi onkostarApi;

    protected final FormService formService;

    protected AbstractTherapieplanService(final IOnkostarApi onkostarApi, final FormService formService) {
        this.onkostarApi = onkostarApi;
        this.formService = formService;
    }

    @Override
    public List<Procedure> findReferencedFollowUpsForSubform(Procedure procedure) {
        if (null == procedure || !"DNPM UF Einzelempfehlung".equals(procedure.getFormName())) {
            return List.of();
        }

        return procedure.getDiseaseIds().stream()
                .flatMap(diseaseId -> onkostarApi.getProceduresForDiseaseByForm(diseaseId, "DNPM FollowUp").stream())
                .filter(p -> p.getValue("LinkTherapieempfehlung").getInt() == procedure.getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Procedure> findReferencedFollowUpsForSubform(int procedureId) {
        var procedure = this.onkostarApi.getProcedure(procedureId);
        if (null == procedure || !"DNPM UF Einzelempfehlung".equals(procedure.getFormName())) {
            return List.of();
        }
        return findReferencedFollowUpsForSubform(procedure);
    }

}
