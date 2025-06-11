# SQL-Queries für DNPM-Formulare

## Abfrage von weiteren Informationen zu Merkmalskatalogeinträgen (Propcat)

Angenommen eine Abfrage liefert für dk_molekulargenetik.entnahmemethode den Wert B und
die zugehörige Version in dk_molekulargenetik.entnahmemethode_propcat_version ist 1171:

```
SELECT code, shortdesc, description FROM property_catalogue_version_entry
    WHERE code = 'B'
    AND property_version_id = 1171
```

Diese Abfrage liefert B, Biopsie, Biopsie.

## Stammdaten zum Patienten anhand SAP-ID für den Personenstamm z.B. 4.

```
SELECT * FROM patient
    WHERE patient.patienten_id = '?' AND patient.personenstamm = 4;
```

## Diagnosedaten zur Erkrankung

```
SELECT dk_diagnose.* FROM dk_diagnose
    JOIN prozedur ON (prozedur.id = dk_diagnose.id)
    JOIN erkrankung_prozedur ON (erkrankung_prozedur.prozedur_id = prozedur.id)
    JOIN erkrankung ON (erkrankung.id = erkrankung_prozedur.erkrankung_id)
    JOIN patient ON (patient.id = prozedur.patient_id)
    WHERE patient.patienten_id = '?' AND erkrankung.tumoridentifikator = ?;
```

## Formular DNPM Klinik/Anamnese inkl. Unterformulare

Formular DNPM Klinik/Anamnese anhand Patienten-(SAP)-ID und Onkostar-Tumor-ID:

```
SELECT dk_dnpm_kpa.* FROM dk_dnpm_kpa
    JOIN prozedur ON (prozedur.id = dk_dnpm_kpa.id)
    JOIN erkrankung_prozedur ON (erkrankung_prozedur.prozedur_id = prozedur.id)
    JOIN erkrankung ON (erkrankung.id = erkrankung_prozedur.erkrankung_id)
    JOIN patient ON (patient.id = prozedur.patient_id)
    WHERE patient.patienten_id = '?' AND erkrankung.tumoridentifikator = ?;
```

## Unterformular Histologie(en)

```
SELECT dk_dnpm_uf_histologie.* FROM dk_dnpm_uf_histologie
    JOIN prozedur ON (prozedur.id = dk_dnpm_uf_histologie.id)
    WHERE prozedur.hauptprozedur_id = ?;
```

## Unterformular ECOG Performance Status Verlauf

```
SELECT dk_dnpm_uf_tumorausbreitung.* FROM dk_dnpm_uf_tumorausbreitung
    JOIN prozedur ON (prozedur.id = dk_dnpm_uf_tumorausbreitung.id)
    WHERE prozedur.hauptprozedur_id = ?;
```

## Unterformular Tumorerkrankung bei Verwandten

```
SELECT dk_dnpm_uf_verwandte.* FROM dk_dnpm_uf_verwandte
    JOIN prozedur ON (prozedur.id = dk_dnpm_uf_verwandte.id)
    WHERE prozedur.hauptprozedur_id = ?;
```

## Unterformular Molekularpathologische Vorbefunde

```
SELECT dk_dnpm_vorbefunde.* FROM dk_dnpm_vorbefunde
    JOIN prozedur ON (prozedur.id = dk_dnpm_vorbefunde.id)
    WHERE prozedur.hauptprozedur_id = ?;
```

## Unterformular Therapielinien

Achtung: Hier enthält der Tabellenname nicht den Bestandteil 'uf' für Unterformular.

```
SELECT dk_dnpm_therapielinie.* FROM dk_dnpm_therapielinie
    JOIN prozedur ON (prozedur.id = dk_dnpm_therapielinie.id)
    WHERE prozedur.hauptprozedur_id = ?; 
```

Im Feld wirkstoffcodes sind die Wirkstoffe als JSON-Array in folgender Form gespeichert, da
Onkostar keine Liste in Unterformularen verarbeiten kann:

| Name      | Bedeutung                               |
|-----------|-----------------------------------------|
| system    | Das verwendete System. ATC oder "other" |
| code      | Der verwendete Wirkstoffcode            |
| substance | Der Name des Wirkstoffs                 |

Beispiel:

```
[
    {"system":"other","code":"Gemcitabin","substance":"Gemcitabin (dFdC)"},
    {"system":"other","code":"Cisplatin ","substance":"Cisplatin (CDDP)"}
]
```

## Formular DNPM Therapieplan inkl. Unterformulare

Formular DNPM Klinik/Anamnese anhand Patienten-(SAP)-ID und Onkostar-Tumor-ID:

```
SELECT dk_dnpm_therapieplan.* FROM dk_dnpm_therapieplan
    JOIN prozedur ON (prozedur.id = dk_dnpm_therapieplan.id)
    JOIN erkrankung_prozedur ON (erkrankung_prozedur.prozedur_id = prozedur.id)
    JOIN erkrankung ON (erkrankung.id = erkrankung_prozedur.erkrankung_id)
    JOIN patient ON (patient.id = prozedur.patient_id)
    WHERE patient.patienten_id = '?' AND erkrankung.tumoridentifikator = ?;
```

Alternativ mit Verweis auf DNPM Klinik/Anamnese

```
...
    WHERE dk_dnpm_therapieplan.ref_dnpm_klinikanamnese = ?;
```

### Unterformular Reevaluation

```
SELECT dk_dnpm_uf_reevaluation .* FROM dk_dnpm_uf_reevaluation
    JOIN prozedur ON (prozedur.id = dk_dnpm_uf_reevaluation .id)
    WHERE prozedur.hauptprozedur_id = ?;
```

Achtung! Gegebenenfalls ist vor dem erneuten Bearbeiten des Formulars der Inhalt eines
einzelnen Reevaluationsauftrags direkt im Hauptformular gespeichert. In dem Fall sind keine
Unterformulare zur Reevaluation gespeichert.

### Unterformular Rebiopsie

```
SELECT dk_dnpm_uf_rebiopsie.* FROM dk_dnpm_uf_rebiopsie
    JOIN prozedur ON (prozedur.id = dk_dnpm_uf_rebiopsie.id)
    WHERE prozedur.hauptprozedur_id = ?;
```

### Unterformular Einzelempfehlung

```
SELECT dk_dnpm_uf_einzelempfehlung.* FROM dk_dnpm_uf_einzelempfehlung
    JOIN prozedur ON (prozedur.id = dk_dnpm_uf_einzelempfehlung.id)
    WHERE prozedur.hauptprozedur_id = ?;
```

#### Wirkstoffe

Im Feld wirkstoffe_json sind die Wirkstoffe als JSON-Array in folgender Form gespeichert, da
Onkostar keine Liste in Unterformularen verarbeiten kann:

| Name   | Bedeutung                                        |
|--------|--------------------------------------------------|
| system | Das verwendete System. "ATC" oder "UNREGISTERED" |
| code   | Der verwendete Wirkstoffcode                     |
| name   | Der Name des Wirkstoffs                          |

Beispiel:

```
[
    {"code":"","name":"PARP-Inhibierung","system":"UNREGISTERED"}
]
```

#### Studien

Im Feld studien_alle_json sind die Wirkstoffe als JSON-Array in folgender Form gespeichert,
da Onkostar keine Liste in Unterformularen verarbeiten kann:

| Name                    | Bedeutung                   |
|-------------------------|-----------------------------|
| studie                  | Name der Studie             |
| system                  | System der Studie(n-ID)     |
| id (und alt auch "nct") | Die Studien-ID              |
| ort                     | Ort der Studiendurchführung |
| internextern            | intern (i) oder extern (e)  |

Beispiel:

```
[
    {"studie":"TestInhibitor","system":"NCT","id":"NCT12345678","nct":"NCT12345678","ort":"Teststadt","internextern":"e"}
]
```
