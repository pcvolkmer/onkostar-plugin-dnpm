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

let histologie = getFieldValue('Histologie');
let diagnosis = getCurrentDisease();

if (histologie.Datum) {
    // OS.Molekulargenetik
    let histologyCode = diagnosis.histologyCode ? diagnosis.histologyCode : 'nicht angegeben';
    let tumorzellgehalt = histologie.Tumorzellgehalt ? `${histologie.Tumorzellgehalt}%` : 'nicht angegeben';
    setFieldValue('Befundtext', `Morphologie: ${histologyCode} (aus Diagnose)\nTumorzellgehalt: ${tumorzellgehalt}\n`);
    setFieldValue('Erstellungsdatum', histologie.Datum);
    setFieldValue('Morphologie', diagnosis.histologyCode);
    setFieldValue('Tumorzellgehalt', histologie.Tumorzellgehalt);
} else if (histologie.HistologieDatum) {
    // OS.Pathologiebefund
    let histologyCode = diagnosis.histologyCode ? diagnosis.histologyCode : 'nicht angegeben';
    setFieldValue('Befundtext', `Morphologie: ${histologyCode}\nTumorzellgehalt: nicht angegeben\n`);
    setFieldValue('Erstellungsdatum', histologie.HistologieDatum);
    setFieldValue('Morphologie', histologie.ICDO3Histologie);
}

