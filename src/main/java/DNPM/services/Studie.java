package DNPM.services;

public class Studie {
    private final String code;
    private final String studiennummer;
    private final String shortDesc;
    private final String description;
    private final int version;

    public Studie(final String code, final String studiennummer, final String shortDesc, final String description, final int version) {
        this.code = code;
        this.studiennummer = studiennummer;
        this.shortDesc = shortDesc;
        this.description = description;
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public String getStudiennummer() {
        return studiennummer;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getDescription() {
        return description;
    }

    public int getVersion() {
        return version;
    }

    public Type getType() {
        if (this.hasNctNumber()) {
            return Type.NCT;
        } else if (this.hasEudraCtNumber()) {
            return Type.EUDRA_CT;
        } else {
            return Type.UNKNOWN;
        }
    }

    private boolean hasNctNumber() {
        return null != studiennummer && studiennummer.toLowerCase().startsWith("nct");
    }

    private boolean hasEudraCtNumber() {
        return null != studiennummer && studiennummer.matches("[0-9]{4}-[0-9]{6}-[0-9]{2}");
    }

    public enum Type {
        NCT,
        EUDRA_CT,
        UNKNOWN
    }
}
