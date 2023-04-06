# Onkostar-Plugin zur Verwendung mit der DNPM-Formularsammlung

## ATC-Codes

Dieses Plugin integriert das ATC-Codes-Plugin vollständig. Dieses kann daher nicht zusätzlich in Onkostar installiert werden.

## Consent

Das Plugin ist auf die Übernahme des DNPM-Consents ausgelegt. Hierzu muss die Einstellung `consentform` festgelegt werden.
Diese Einstellung muss manuell in der Datenbank angelegt werden und kann danach in Onkostar verändert werden.

```
INSERT INTO einstellung (name, wert, kategorie, beschreibung)
VALUES (
 'consentform',
 'MR.Consent',
 'DNPM',
 'Zu verwendendes Consent-Formular'
);
```

Aktuell werden folgende Consent-Formulare unterstützt:

* `MR.Consent`
* `Excel-Formular` (UKW - Beinhaltet Consent-Angaben)

Eine Übernahme von Consent-Daten aus unbekannten Formularen ist nur manuell möglich.


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

### Mapping Systemtherapie-Formular zu Prozedurwerten

Das Formular `DNPM KlinikAnamnese` verwendet eine Backend-Service-Funktion zum Ermitteln vorliegender Therapielinien. Für die Formulare

* `OS.Systemische Therapie`
* `OS.Systemische Therapie.VarianteUKW`

wird analog zum Mapping MTB auf Therapieplan-Protokollauszug eine formularspezifische Entscheidung getroffen, welcher Mapper zur
Laufzeit verwendet werden soll. Der Mapper muss hierbei das Interface `ProzedurToProzedurwerteMapper` implementieren.

In der Klasse `DefaultSystemtherapieService` wird zur Laufzeit der erforderliche Mapper für das verwendete Formular ausgewählt.

An dieser Stelle kann auch eine eigene Implementierung - eine neue Klasse, die das Interface `ProzedurToProzedurwerteMapper` implementiert - 
integriert werden, indem das zu verwendende Formular (Formularname) und die zu verwendende Mapping-Klasse für den Formularnamen angegeben wird.

Hierbei kann in der Einstellung `systemtherapieform` festgelegt werden, dass ein anderes Formular als "OS.Systemische Therapie" verwendet werden soll.
Diese Einstellung muss manuell in der Datenbank angelegt werden und kann danach in Onkostar verändert werden.

```
INSERT INTO einstellung (name, wert, kategorie, beschreibung)
VALUES (
 'systemtherapieform',
 'OS.Systemische Therapie',
 'DNPM',
 'Zu verwendendes Formular für die systemische Therapie'
);
```

## Bauen des Plugins

Für das Bauen des Plugins ist zwingend JDK in Version 11 erforderlich.
Spätere Versionen des JDK beinhalten einige Methoden nicht mehr, die von Onkostar und dort benutzten Libraries verwendet
werden.

Voraussetzung ist das Kopieren der Datei `onkostar-api-2.11.1.1.jar` (oder neuer) in das Projektverzeichnis `libs`.

Weiterhin verwendet dieses Plugin das [ATC-Codes-Plugin](https://github.com/CCC-MF/onkostar-plugin-atccodes).
Die zugehörige JAR-Datei muss ebenfalls in das Projektverzeichnis `libs` kopiert werden. Aktuell wird Version 0.5.0 verwendet.

**_Hinweis_**: Bei Verwendung einer neueren Version der Onkostar-API oder des ATC-Codes-Plugins
muss die Datei `pom.xml` entsprechend angepasst werden.

Danach Ausführen des Befehls:

```shell
./mvnw package
```



