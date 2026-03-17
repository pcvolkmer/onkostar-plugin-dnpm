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

package dev.dnpm.oshelper.atc.services;


import dev.dnpm.oshelper.atc.AgentCode;
import org.jspecify.annotations.NullMarked;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Common interface for agent code services
 *
 * @author Paul-Christian Volkmer
 * @since 2.0.0
 */
public interface AgentCodeService {

    /**
     * Filters the provided list of AgentCodes to include only the latest available versions
     * for each unique code name and, if required, code.
     * If the input list is empty or null, an empty list will be returned.
     *
     * @param agentCodes A list of {@link AgentCode} objects to be filtered. Each object in the list
     *                   should contain information about the agent's code, name, system, and version.
     * @param classifier A function that returns a string value to be used as a key for grouping agent codes.
     * @return A list of {@link AgentCode} objects representing the latest available versions of unique agent codes.
     * If no valid inputs are provided, an empty list is returned.
     */
    @NullMarked
    static List<AgentCode> latestAgentCodeVersions(List<AgentCode> agentCodes, Function<AgentCode, String> classifier) {
        return latestAvailable(agentCodes, classifier);
    }

    /**
     * Function that extracts the name of an {@link AgentCode} object.
     * This function is used as a classifier for grouping agent codes by their name.
     */
    Function<AgentCode, String> byCodeOnly = AgentCode::getName;

    /**
     * Function that extracts the name and code of an {@link AgentCode} object.
     * This function is used as a classifier for grouping agent codes by their name and code.
     */
    Function<AgentCode, String> byNameAndCode = (agentCode ->
            String.format("%s_%s", agentCode.getCode(), agentCode.getName())
    );


    @NullMarked
    private static List<AgentCode> latestAvailable(
            List<AgentCode> agentCodes,
            Function<AgentCode, String> classifier
    ) {
        return agentCodes.stream()
                .collect(groupingBy(classifier)).values().stream()
                .map(list ->
                        list.stream()
                                .sorted(Comparator.comparing(AgentCode::getCode))
                                .max((ac1, ac2) -> {
                                    if (null == ac1.getVersion()) {
                                        return -1;
                                    } else if (null == ac2.getVersion()) {
                                        return 1;
                                    }
                                    return ac1.getVersion().compareTo(ac2.getVersion());
                                })
                                .orElse(null)
                )
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Queries source for agents with name and code starting with query string.
     * If size is zero, all available results will be returned.
     *
     * @param query The query string
     * @param size  Maximal amount of responses
     * @return A list with agent codes
     */
    List<AgentCode> findAgentCodes(String query, int size);
}
