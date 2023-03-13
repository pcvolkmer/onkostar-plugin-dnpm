package DNPM.services;

public class Studie {
    private final String code;
    private final String shortDesc;
    private final String description;
    private final int version;

    public Studie(final String code, final String shortDesc, final String description, final int version) {
        this.code = code;
        this.shortDesc = shortDesc;
        this.description = description;
        this.version = version;
    }

    public String getCode() {
        return code;
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
}
