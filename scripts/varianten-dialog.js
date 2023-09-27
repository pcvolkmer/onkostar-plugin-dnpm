const availableStore = new Ext.data.ArrayStore({
    fields: [
        {name: 'id'},
        {name: 'ergebnis'},
        {name: 'gen'},
        {name: 'exon'},
        {name: 'pathogenitaetsklasse'}
    ]
});

const selectedStore = new Ext.data.ArrayStore({
    fields: [
        {name: 'id'},
        {name: 'ergebnis'},
        {name: 'gen'},
        {name: 'exon'},
        {name: 'pathogenitaetsklasse'}
    ]
});

let pluginRequestsDisabled = false;
let available = [];
let selected = [];
let blockIndex = null;

const findButtonFieldFormInformation = function(context) {
    const findElemId = function(elem) {
        if (elem.tagName === 'BODY') {
            return undefined;
        }

        if (elem.tagName === 'TABLE') {
            return elem.id;
        }

        return findElemId(elem.parentElement);
    }

    const formInfo = function(formItem, blockIndex = undefined) {
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
            let formItem = context.genericEditForm.down('#'+elemId);
            if (formItem) {
                return formInfo(formItem);
            }
        }
    }

    return undefined;
}

const request = function (id) {
    if (pluginRequestsDisabled) return;
    executePluginMethod(
        'EinzelempfehlungAnalyzer',
        'getVariants',
        {id: id},
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
    return [item.id, item.ergebnis, item.gen, item.exon, item.pathogenitaetsklasse];
}

const addItem = function (item) {
    if (selected.map(item => item.id).indexOf(item.id) >= 0) {
        return;
    }
    selected.push(item);
    const extData = selected.map(itemMapping);
    selectedStore.loadData(extData);
};

const removeItem = function (index) {
    selected.splice(index, 1);
    const extData = selected.map(itemMapping);
    selectedStore.loadData(extData);
};

const save = () => {
    const names = selected.map((item) => {
        return `${item.ergebnis}: ${item.gen}, ${item.exon}, ${item.pathogenitaetsklasse}`;
    }).join("\n");

    this.getFieldByEntriesArray('stmolaltalle', blockIndex).setValue(names);
    this.getFieldByEntriesArray('stmolaltvariantejson', blockIndex).setValue(JSON.stringify(selected));
};

const onFailure = function() {
    pluginRequestsDisabled = true;
    Ext.MessageBox.show({
        title: 'Hinweis',
        msg: 'Plugin "DNPM" nicht verfügbar.',
        buttons: Ext.MessageBox.OKCANCEL
    });
};

const onSuccess = function(d) {
    available = d;
    const extData = available.map(itemMapping);
    availableStore.loadData(extData);
}

const showDialog = function (procedureId) {
    let selectedItemIndex = -1;
    let deselectedItemIndex = -1;

    try {
        selected = JSON.parse(getFieldValue('stmolaltvariantejson', blockIndex));
        const extData = selected.map(itemMapping);
        selectedStore.loadData(extData);
    } catch (e) {
        selected = [];
        const extData = selected.map(itemMapping);
        selectedStore.loadData(extData);
    }

    const gridColumns = [
        {header: 'Ergebnis', width: 240, sortable: false, dataIndex: 'ergebnis'},
        {header: 'Gen', width: 80, sortable: false, dataIndex: 'gen'},
        {header: 'Exon', width: 80, sortable: false, dataIndex: 'exon'},
        {header: 'Pathogenitätsklasse', sortable: false, dataIndex: 'pathogenitaetsklasse'},
    ];

    const availableGrid = new Ext.grid.GridPanel({
        title: 'Verfügbar',
        store: availableStore,
        loadMask: true,
        border: true,
        columns: gridColumns,
        flex: 1,
        overflowY: 'scroll',
        listeners: {
            itemclick: (dv, record, item, index) => {
                selectedItemIndex = index;
                Ext.getCmp('btnAdd').setDisabled(false);
            },
            itemdblclick: (dv, record, item, index) => {
                selectedItemIndex = -1
                addItem(available[index]);
                Ext.getCmp('btnAdd').setDisabled(true);
            }
        }
    });

    const selectedGrid = new Ext.grid.GridPanel({
        title: 'Ausgewählt',
        store: selectedStore,
        loadMask: true,
        border: true,
        columns: gridColumns,
        flex: 1,
        overflowY: 'scroll',
        listeners: {
            itemclick: (dv, record, item, index) => {
                deselectedItemIndex = index;
                Ext.getCmp('btnRm').setDisabled(false);
            },
            itemdblclick: (dv, record, item, index) => {
                deselectedItemIndex = -1
                removeItem(index);
                Ext.getCmp('btnRm').setDisabled(true);
            }
        }
    });

    const gridLayout = Ext.create('Ext.Panel', {
        flex: 1,
        layout: {
            type: 'hbox',
            align: 'stretch'
        },
        items: [availableGrid, { xtype: 'splitter' }, selectedGrid]
    });

    const layout = Ext.create('Ext.Panel', {
        flex: 1,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [gridLayout]
    });

    Ext.create('Ext.window.Window', {
        title: 'Variante auswählen',
        height: 600,
        width: 1080,
        layout: 'fit',
        items: [layout],
        buttons: [{
            id: 'btnAdd',
            text: 'Hinzufügen',
            disabled: true,
            handler: () => {
                addItem(available[selectedItemIndex]);
                Ext.getCmp('btnAdd').setDisabled(true);
            }
        }, {
            id: 'btnRm',
            text: 'Entfernen',
            disabled: true,
            handler: () => {
                removeItem(deselectedItemIndex);
                Ext.getCmp('btnRm').setDisabled(true);
            }
        }, {
            text: 'Übernehmen',
            cls: 'onko-btn-cta',
            handler: () => {
                save();
                let win = Ext.WindowManager.getActive();
                if (win) {
                    win.close();
                }
            }
        }]
    }).show();

    request(procedureId);
};

let buttonFieldFormInformation = findButtonFieldFormInformation(this);
if (buttonFieldFormInformation && buttonFieldFormInformation.blockIndex) {
    blockIndex = buttonFieldFormInformation.blockIndex;
}

var procedureId = getFieldValue('refosmolekulargenetik', blockIndex).id;

showDialog(procedureId);