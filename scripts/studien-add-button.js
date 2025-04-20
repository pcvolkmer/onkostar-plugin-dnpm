let addText = function() {
    let v = getFieldValue('studienalle');
    let ie = getFieldValue('studieinternextern') == 'i' ? ' (intern)' : ' (extern)';
    v = v + getFieldValue('studienct') + '; ' + getFieldValue('studie') + ie + '; ' + getFieldValue('studieort') + '\n';
    setFieldValue('studienalle', v);
}

var addJSON = function() {
    let v = [];
    try {
        v = JSON.parse(getFieldValue('studienallejson'));
        if (!Array.isArray(v)) {
            v = [];
        }
    } catch (e) {
        v = [];
    }
    v.push({
        studie: getFieldValue('studie'),
        system: getFieldValue('studiensystem'),
        // New: ID
        id: getFieldValue('studienct'),
        // Old: NCT (from NCT-ID)
        nct: getFieldValue('studienct'),
        ort: getFieldValue('studieort'),
        internextern: getFieldValue('studieinternextern')
    });
    setFieldValue('studienallejson', JSON.stringify(v));
}

addText();
addJSON();

setFieldValue('studie', '');
setFieldValue('studiensystem', '');
setFieldValue('studienct', '');
setFieldValue('studieort', '');
setFieldValue('studieinternextern', '');
