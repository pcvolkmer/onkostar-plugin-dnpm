# Format der Migrationsdateien

Die Migrationsdateien sollen fortlaufend aufsteigend nummeriert sein, mit `.sql` enden.

Zudem sollen sie immer Angaben zum betreffenden Formular und Revision enthalten:

```sql
-- form: DNPM Beispiel
-- min-rev: 1
-- max-rev: 2

ALTER TABLE dk_dnpm_beispiel
    MODIFY COLUMN langer_text text;
```

Ist keine Angabe zum Formularnamen angegeben, wird die Migration immer ausgeführt,
anderenfalls nur für eine entsprechende Revision des angegebenen Formulars.
Wenn nicht angegeben, ist die minimal erforderliche Revision ist standardmäßig 0, die maximale 99999999999999.