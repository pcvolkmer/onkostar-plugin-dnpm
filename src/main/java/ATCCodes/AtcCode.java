package ATCCodes;

import java.util.Objects;

/**
 * ATC-Code as used in WHO XML file
 *
 * @author Paul-Christian Volkmer
 * @since 0.1.0
 */
public class AtcCode implements AgentCode {

    private final String code;
    private final String name;

    public AtcCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public CodeSystem getSystem() {
        return CodeSystem.ATC;
    }

    @Override
    public int compareTo(final AgentCode agentCode) {
        return this.name.toLowerCase().compareTo(agentCode.getName().toLowerCase());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentCode otherAgentCode = (AgentCode) o;
        return Objects.equals(code.toLowerCase(), otherAgentCode.getCode().toLowerCase())
                && Objects.equals(name.toLowerCase(), otherAgentCode.getName().toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code.toLowerCase(), name.toLowerCase());
    }

    /**
     * Prüft auf gültiges ATCCode-Schema ab Ebene 2
     * @param code Der zu prüfende Code
     * @return Gibt <code>true</code> zurück, wenn der angegebene Code dem ATCCode-Schema entspricht
     */
    public static boolean isAtcCode(String code) {
        return null != code
                && ! code.isBlank()
                && code.matches("[ABCDGHJLMNPRSV][0-2][1-9]([A-Z]([A-Z]([0-9]{2})?)?)?");
    }
}