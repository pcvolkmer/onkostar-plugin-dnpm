package ATCCodes.services;

import ATCCodes.AgentCode;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract {@link AgentCodeService} for use with files that will load information into memory
 *
 * @author Paul-Christian Volkmer
 * @since 0.1.0
 */
public abstract class FileBasedAgentCodeService implements AgentCodeService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final List<AgentCode> codeList = new ArrayList<>();

    FileBasedAgentCodeService(final ResourceLoader resourceLoader) {
        this.codeList.addAll(parseFile(resourceLoader));
    }

    static String getFilePath(final String filename) {
        String pluginPathPart = "onkostar/files/onkostar/plugins/onkostar-plugin-atccodes";

        if (SystemUtils.IS_OS_WINDOWS) {
            return String.format("file:///c:/%s/%s", pluginPathPart, filename);
        } else if (SystemUtils.IS_OS_LINUX) {
            return String.format("file:///opt/%s/%s", pluginPathPart, filename);
        }
        return filename;
    }

    protected abstract List<AgentCode> parseFile(final ResourceLoader resourceLoader);

    /**
     * Queries source for agents code starting with or name containing query string.
     * If size is zero, all available results will be returned.
     *
     * @param query The query string
     * @param size  Maximal amount of responses
     * @return A list with agent codes
     */
    @Override
    public List<AgentCode> findAgentCodes(final String query, final int size) {
        var resultStream = this.codeList.stream().filter(agentCode ->
                agentCode.getCode().toLowerCase().startsWith(query.toLowerCase())
                        || agentCode.getName().toLowerCase().contains(query.toLowerCase())
        );

        if (size > 0) {
            return resultStream.limit(size).collect(Collectors.toList());
        }
        return resultStream.collect(Collectors.toList());
    }

}