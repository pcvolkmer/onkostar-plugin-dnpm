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
