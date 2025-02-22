/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.dnpm.dto;

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
