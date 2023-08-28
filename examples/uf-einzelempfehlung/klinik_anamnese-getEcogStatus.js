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

        let uf = resp.result.map(item => {
            let date = new Date(item.date).toISOString().split('T')[0];
            let ecog = [];
            ecog.val = item.status;
            ecog.version = version;
            return {
                Datum: [date, 'exact'], ECOG: ecog
            };
        });
        setFieldValue('ECOGVerlauf', uf);
    }
}, false);