package ATCCodes;

/**
 * Common Agent Code definition
 *
 * @author Paul-Christian Volkmer
 * @since 0.1.0
 */
public interface AgentCode extends Comparable<AgentCode> {
    String getCode();

    String getName();

    CodeSystem getSystem();
}