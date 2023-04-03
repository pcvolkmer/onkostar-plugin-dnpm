# Onkostar-Plugin zur Verwendung mit der DNPM-Formularsammlung

## Consent

Das Plugin ist auf die Übernahme des DNPM-Consents ausgelegt. Hierzu muss die Einstellung `consentform` festgelegt werden.
Diese Einstellung muss manuell in der Datenbank angelegt werden und kann danach in Onkostar verändert werden.

```
INSERT INTO einstellung (name, wert, kategorie, beschreibung)
VALUES (
 'consentform',
 'MR.Consent',
 'DNPM',
 '',
 'Zu verwendendes Consent-Formular'
);
```

Aktuell werden folgende Consent-Formulare unterstützt:

* `MR.Consent`


## Therapieplan

Soll das automatische Befüllen der Unterformulare *Einzelempfehlung* und *Rebiopsie* nicht durchgeführt werden, weil es mehrere MTBs je MTB-Episode gibt, so muss die Einstellung `mehrere_mtb_in_mtbepisode` vorhanden sein und auf den Wert `true` gesetzt sein.

```
INSERT INTO einstellung (name, wert, kategorie, optionen, beschreibung)
VALUES (
 'mehrere_mtb_in_mtbepisode',
 'true',
 'DNPM',
 '[{"key": "true", "value": "Ja"},{"key": "false", "value": "Nein"}]',
 'Angabe, ob mehrere MTBs je MTB-Episode verwendet werden.'
);
```

### Mapping MTB zu Therapieplan-Protokollauszug

Das Plugin ermöglicht die Übernahme von Inhalten aus einem MTB in den Protokollauszug des DNPM Therapieplans. Für die Formulare

* `OS.Tumorkonferenz`
* `OS.Tumorkonferenz.VarianteUKW`
* `MR.MTB_Anmeldung`

liegen bereits Implementierungen vor. Um eigene Implementierungen vorzunehmen, die sich an andere Formulare oder Formularvarianten richtet,
muss hierzu das Interface `ProcedureToProtocolMapper` implementiert werden. Dazu muss die Methode `apply(Procedure)` derart implementiert werden,
sodass aus einer Prozedur ein entsprechender Abschnitt als Text für den Protokollauszug gewandelt wird.

Als Rückgabewert wird hierbei ein Wert vom Typ `Optional<String>` erwartet, dabei z.B. `Optional.of("Text")`, wenn ein zu verwendender Text oder
z.B. `Optional.empty()` wenn kein zu verwendender Text zurückgegeben wird.

Anschließend ist das Mapping in `DefaultMtbService` in der Methode `procedureToProtocolMapper(Procedure)` einzutragen, beispielsweise durch

```
...
    case "Custom.Neuekonferenz":
        return new CustomNeuekonferenzToProtocolMapper();
...
```

Idealerweise werden entsprechende UnitTests hinzugefügt.



