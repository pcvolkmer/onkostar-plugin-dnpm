package dev.dnpm.dto;

public class Studie {
    private final String kategorieName;
    private final String code;
    private final String studiennummer;
    private final String shortDesc;
    private final String description;
    private final int version;

    private final boolean active;

    public Studie(final String kategorieName, final int version, final String code, final String studiennummer, final String shortDesc, final String description, final boolean active) {
        this.kategorieName = kategorieName;
        this.version = version;
        this.code = code;
        this.studiennummer = studiennummer;
        this.shortDesc = shortDesc;
        this.description = description;
        this.active = active;
    }

    public String getKategorieName() {
        return kategorieName;
    }

    public int getVersion() {
        return version;
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

    public boolean isActive() {
        return active;
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
        return null != studiennummer && studiennummer.matches("\\d{4}-\\d{6}-\\d{2}");
    }

    public enum Type {
        NCT,
        EUDRA_CT,
        UNKNOWN
    }
}
