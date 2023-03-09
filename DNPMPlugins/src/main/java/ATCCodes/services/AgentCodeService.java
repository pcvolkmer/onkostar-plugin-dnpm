package ATCCodes.services;

import ATCCodes.AgentCode;

import java.util.List;

/**
 * Common interface for agent code services
 *
 * @author Paul-Christian Volkmer
 */
public interface AgentCodeService {

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