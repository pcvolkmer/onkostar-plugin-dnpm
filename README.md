# Onkostar-Plugin zur Verwendung mit der DNPM-Formularsammlung

## Therapieplan

Soll das automatische Befüllen der Unterformulare *Einzelempfehlung* und *Rebiopsie* nicht durchgeführt werden, weil es mehrere MTBs je MTB-Episode gibt, so muss die Einstellung `mehrere_mtb_in_mtbepisode` vorhanden sein und auf den Wert `true` gesetzt sein.

```
INSERT INTO einstellung (name, wert, kategorie, optionen, beschreibung)
VALUES (
 'mehrere_mtb_in_mtbepisode',
 'true',
 'Dokumentation',
 '[{"key": "true", "value": "Ja"},{"key": "false", "value": "Nein"}]',
 'Angabe, ob mehrere MTBs je MTB-Episode verwendet werden.'
);
```



