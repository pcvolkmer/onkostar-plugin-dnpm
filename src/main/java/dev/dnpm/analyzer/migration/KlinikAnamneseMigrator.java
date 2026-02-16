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

package dev.dnpm.analyzer.migration;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;

import java.util.regex.Pattern;

public class KlinikAnamneseMigrator extends Migrator {

    public KlinikAnamneseMigrator(IOnkostarApi onkostarApi) {
        super(onkostarApi, "DNPM KPA");
    }

    @Override
    protected void migrateProcedure(Procedure procedure) {
        migrateArtDerKrankenkasse(procedure);
    }

    private void migrateArtDerKrankenkasse(Procedure procedure) {
        if (procedure.getRevision() > 232 && procedure.getValue("ArtDerKrankenkasse") == null) {
            var patient = procedure.getPatient();
            if (null != patient) {
                var insuranceNumber = patient.getInsuranceNumber();
                if (Pattern.compile("[A-Z]\\d{9}").matcher(insuranceNumber).matches()) {
                    procedure.setValue("ArtDerKrankenkasse", new Item("ArtDerKrankenkasse", "GKV"));
                }
                else if (Pattern.compile("16\\d{7}|950\\d{6}").matcher(insuranceNumber).matches()) {
                    procedure.setValue("ArtDerKrankenkasse", new Item("ArtDerKrankenkasse", "PKV"));
                }
                else if ("970000011".equals(insuranceNumber)) {
                    procedure.setValue("ArtDerKrankenkasse", new Item("ArtDerKrankenkasse", "SEL"));
                }
            }
        }
    }

}
