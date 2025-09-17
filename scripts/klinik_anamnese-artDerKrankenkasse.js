// Art der Krankenkasse
if (new RegExp(/[A-Z]\d{9}/).test(patient.versicherungsnummer)) {
  setFieldValue('ArtDerKrankenkasse', 'GKV');
}
else if (new RegExp(/16\d{7}|950\d{6}/).test(patient.versicherungsnummer)) {
  setFieldValue('ArtDerKrankenkasse', 'PKV');
}
else if ('970000011' == patient.versicherungsnummer) {
  setFieldValue('ArtDerKrankenkasse', 'SEL');
}
