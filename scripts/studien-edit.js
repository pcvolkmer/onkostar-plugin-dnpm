let text = getFieldValue('studienalle');
let json = [];
let newJson = [];

try {
    json = JSON.parse(getFieldValue('studienallejson'));
    if (!Array.isArray(json)) {
        json = [];
    }
} catch (e) {
    json = [];
}

if (typeof text === 'string' || text instanceof String) {
    text.split('\n').forEach(line => {
        let id = line.split(';')[0];
        json.forEach(entry => {
            if (id === entry.nct) {
                newJson.push(entry);
            }
        });
    });
    setFieldValue('studienallejson', JSON.stringify(newJson));
} else {
    setFieldValue('studienallejson', JSON.stringify([]));
}
