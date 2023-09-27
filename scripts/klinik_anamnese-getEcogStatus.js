// To be included in Script "Beim Neuanlegen" of form "DNPM Klink/Anamnese"

executePluginMethod('DNPMHelper', 'getEcogStatus', {PatientId: getPatient().id}, (resp) => {
    if (resp.status.code === 1) {
        // Hack: Get version id of ECOG status as stored in Database
        // by using initial empty entry and its version.
        // Since OS always creates an initial empty entry for subforms
        // this can be used to get required version id from within a form script.
        let version = getFieldValue('ECOGVerlauf')[0].ECOG.version;

        // Abort if no version available.
        if (version == null) {
            return;
        }

        let uf = resp.result
            .map(item => {
                let dateOffset = new Date(item.date).getTimezoneOffset() * -60 * 1000;
                let date = new Date(new Date(item.date).getTime() + dateOffset).toISOString().match(/^\d{4}-\d{2}-\d{2}/);
                let ecog = [];
                ecog.val = item.status;
                ecog.version = version;
                return {
                    Datum: [date ? date[0] : null, 'exact'], ECOG: ecog
                };
            })
            // Ignore items without valid values
            .filter(item => item.Datum[0] && (item.ECOG >= 0 && item.ECOG <= 5));
        setFieldValue('ECOGVerlauf', uf);
    }
}, false);