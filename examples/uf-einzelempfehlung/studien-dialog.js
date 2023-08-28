const availableStore = new Ext.data.ArrayStore({
    fields: [
        {name: 'kategorieName'},
        {name: 'version'},
        {name: 'code'},
        {name: 'type'},
        {name: 'studiennummer'},
        {name: 'shortDesc'},
        {name: 'description'}
    ]
});

let pluginRequestsDisabled = false;

const findButtonFieldFormInformation = function (context) {
    const findElemId = function (elem) {
        if (elem.tagName === 'BODY') {
            return undefined;
        }

        if (elem.tagName === 'TABLE') {
            return elem.id;
        }

        return findElemId(elem.parentElement);
    }

    const formInfo = function (formItem, blockIndex = undefined) {
        if (formItem.xtype === 'buttonField') {
            return formInfo(formItem.ownerCt, formItem.blockIndex);
        }

        if (formItem.xtype === 'panel' || formItem.xtype === 'sectionField') {
            return formInfo(formItem.ownerCt, blockIndex);
        }

        if (formItem.xtype === 'subformField') {
            return {
                isSubform: true,
                formName: formItem.formName,
                subformFieldName: formItem.subformName,
                blockIndex: blockIndex
            };
        }

        if (formItem.xtype === 'form') {
            return {
                isSubform: false,
            };
        }

        console.warn('No information found!');
        return undefined;
    }

    if (context.genericEditForm && document.activeElement.tagName === 'BUTTON') {
        let elemId = findElemId(document.activeElement);
        if (elemId) {
            let formItem = context.genericEditForm.down('#' + elemId);
            if (formItem) {
                return formInfo(formItem);
            }
        }
    }

    return undefined;
}

const request = function (query, includeInactive) {
    if (pluginRequestsDisabled) return;
    executePluginMethod(
        'EinzelempfehlungAnalyzer',
        'getStudien',
        includeInactive ? {q: query, inactive: true} : {q: query},
        function (response) {
            if (response.status.code < 0) {
                onFailure();
                return;
            }
            onSuccess(response.result);
        },
        false
    );
};

const itemMapping = function (item) {
    return [item.kategorieName, item.version, item.code, item.type, item.studiennummer, item.shortDesc, item.description];
}

const onFailure = function () {
    pluginRequestsDisabled = true;
    Ext.MessageBox.show({
        title: 'Hinweis',
        msg: 'Plugin "DNPM" nicht verfügbar.',
        buttons: Ext.MessageBox.OKCANCEL
    });
};

const onSuccess = function (d) {
    available = d;
    const extData = available.map(itemMapping);
    availableStore.loadData(extData);
}

const save = (selectedItemIndex) => {
    this.getFieldByEntriesArray('studie', blockIndex).setValue(available[selectedItemIndex].shortDesc);
    this.getFieldByEntriesArray('studienct', blockIndex).setValue(available[selectedItemIndex].studiennummer);
}

const showDialog = function (blockIndex) {
    let selectedItemIndex = -1;
    let queryString = '';
    let includeInactive = false;

    const gridColumns = [
        {header: 'Kategorie', width: 80, sortable: false, dataIndex: 'kategorieName'},
        {header: 'Version', width: 80, sortable: false, dataIndex: 'version'},
        {header: 'Typ', width: 120, sortable: false, dataIndex: 'type'},
        {header: 'Studiennummer', width: 120, sortable: true, dataIndex: 'studiennummer'},
        {header: 'Name', width: 320, sortable: true, dataIndex: 'shortDesc'},
        {header: 'Beschreibung', width: 400, sortable: false, dataIndex: 'description'}
    ];


    const query = new Ext.form.field.Text({
        name: 'query',
        fieldLabel: 'Suche',
        padding: 8,
        listeners: {
            change: (f) => {
                queryString = f.value;
                request(queryString, includeInactive);
            }
        }
    });

    const inactiveSelection = new Ext.form.field.Checkbox({
        name: 'inactive',
        fieldLabel: 'Inaktive Studien einschließen',
        labelWidth: 240,
        padding: 8,
        listeners: {
            handler: (_, checked) => {
                includeInactive = checked;
                request(queryString, includeInactive);
            }
        }
    });

    const availableGrid = new Ext.grid.GridPanel({
        title: 'Verfügbare Studien',
        store: availableStore,
        loadMask: true,
        border: true,
        columns: gridColumns,
        flex: 1,
        overflowY: 'scroll',
        listeners: {
            itemclick: (dv, record, item, index) => {
                selectedItemIndex = index;
            },
            itemdblclick: (dv, record, item, index) => {
                save(selectedItemIndex);
                let win = Ext.WindowManager.getActive();
                if (win) {
                    win.close();
                }
            }
        }
    });

    const layout = Ext.create('Ext.Panel', {
        flex: 1,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [query, inactiveSelection, availableGrid]
    });

    Ext.create('Ext.window.Window', {
        title: 'Studienauswahl',
        height: 600,
        width: 1080,
        layout: 'fit',
        items: [layout],
        buttons: [{
            id: 'btnAdd',
            text: 'Studie auswählen',
            handler: () => {
                save(selectedItemIndex);
                let win = Ext.WindowManager.getActive();
                if (win) {
                    win.close();
                }
            }
        }, {
            text: 'Abbrechen',
            cls: 'onko-btn-cta',
            handler: () => {
                let win = Ext.WindowManager.getActive();
                if (win) {
                    win.close();
                }
            }
        }]
    }).show();

    request();
};

let buttonFieldFormInformation = findButtonFieldFormInformation(this);
if (buttonFieldFormInformation && buttonFieldFormInformation.blockIndex) {
    blockIndex = buttonFieldFormInformation.blockIndex;
    showDialog(blockIndex);
}
