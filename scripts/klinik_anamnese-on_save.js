let uf = Array.from(getFieldValue('DNPMTherapielinie'))
    .filter(tl => tl.Beginn[0] || tl.Ende[0] || tl.Wirkstoffe || tl.WirkstoffCodes || tl.Ergebnis.val || tl.Beendigung.val || tl.Abbruchgrund)
    .sort((tl1, tl2) => { return tl1.Beginn[0] > tl2.Beginn[0] })
    .map((tl, idx) => {
        tl.Nummer = idx+1;
        return tl;
    });

setFieldValue('DNPMTherapielinie', uf);

if (uf.length > 0){
    setFieldValue('AnzahlTherapielinien', uf.length);
    setFieldValue('Therapiebeginn', uf[uf.length-1].Beginn[0]);
    setFieldValue('Therapieende', uf[uf.length-1].Ende[0]);
} else {
    setFieldValue('Therapiebeginn', [null, 'exact']);
    setFieldValue('Therapieende',  [null, 'exact']);
}