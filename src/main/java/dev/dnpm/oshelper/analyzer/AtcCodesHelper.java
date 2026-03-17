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

package dev.dnpm.oshelper.analyzer;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.AnalyzerRequirement;
import dev.dnpm.oshelper.atc.AgentCode;
import dev.dnpm.oshelper.atc.services.AgentCodeService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AtcCodesHelper extends BackendService {

    private final List<AgentCodeService> agentCodeServices;

    public AtcCodesHelper(List<AgentCodeService> agentCodeServices) {
        this.agentCodeServices = agentCodeServices;
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isRelevantForDeletedProcedure() {
        return false;
    }

    @Override
    public boolean isRelevantForAnalyzer(Procedure procedure, Disease disease) {
        return false;
    }

    @Override
    public boolean isSynchronous() {
        return false;
    }

    @Override
    public AnalyzerRequirement getRequirement() {
        return AnalyzerRequirement.PROCEDURE;
    }

    /**
     * Return list with ATC codes and agents.
     * Usage in script:
     *
     * <pre>
     *      executePluginMethod(
     *          'AtcCodesHelper',
     *          'query',
     *          { q: '', size: 10 },
     *          function (result) {console.log(result);},
     *          false
     *      );
     * </pre>
     *
     * @param input The data Map
     * @return The result list filtered by input
     */
    public List<AgentCode> query(final Map<String, Object> input) {
        String query = "";
        if (null != input.get("q")) {
            query = input.get("q").toString();
        }

        int size = Integer.parseInt(input.get("size").toString());
        if (size == 0) {
            size = 10;
        }
        var result = new ArrayList<AgentCode>();
        for (var agentCodeService : this.agentCodeServices) {
            result.addAll(agentCodeService.findAgentCodes(query, size));
        }
        return result.stream().distinct().sorted().collect(Collectors.toList());
    }
}
