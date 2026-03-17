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

package dev.dnpm.oshelper.dto;

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
