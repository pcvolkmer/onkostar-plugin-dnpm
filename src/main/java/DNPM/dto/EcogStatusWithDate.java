package DNPM.dto;

import org.springframework.util.Assert;

import java.util.Date;

/**
 * Datenklasse zum Abbilden des ECOG-Status und Datum
 */
public class EcogStatusWithDate {
    private final Date date;
    private final String status;

    public EcogStatusWithDate(Date date, String status) {
        Assert.notNull(date, "Date cannot be null");
        Assert.hasText(status, "Status cannot be empty String");
        Assert.isTrue(isValidEcogCode(status), "Not a valid ADT.LeistungszustandECOG code");
        this.date = date;
        this.status = status;
    }

    private boolean isValidEcogCode(String status) {
        switch (status) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "U":
                return true;
            default:
                return false;
        }
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
