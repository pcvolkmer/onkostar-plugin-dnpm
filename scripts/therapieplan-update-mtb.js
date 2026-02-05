var datum = getFieldValue('referstemtb').Datum;
setFieldValue('datum', datum);

var text = "";

var fragestellung = getFieldValue('referstemtb').Fragestellung;

if (fragestellung) {
    text = text + `Fragestellung:\n${fragestellung}\n\n`;
}

var empfehlung = getFieldValue('referstemtb').Empfehlung;

if (empfehlung) {
    text = text + `Empfehlung:\n${empfehlung}`;
}

setFieldValue('protokollauszug', text.trim());