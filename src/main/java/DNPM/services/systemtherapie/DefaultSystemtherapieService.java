package DNPM.services.systemtherapie;

import DNPM.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Standardimplementierung des Systemtherapieservices
 *
 * @since 0.2.0
 */
public class DefaultSystemtherapieService implements SystemtherapieService {

    private final IOnkostarApi onkostarApi;

    private final SettingsService settingsService;

    public DefaultSystemtherapieService(final IOnkostarApi onkostarApi, final SettingsService settingsService) {
        this.onkostarApi = onkostarApi;
        this.settingsService = settingsService;
    }

    /**
     * Ermittelt eine Zusammenfassung der systemischen Therapien für eine Erkrankung
     *
     * @param diseaseId Die ID der Erkrankung
     * @return Zusammenfassung der systemischen Therapien
     */
    @Override
    public List<Map<String, String>> getSystemischeTherapienFromDiagnose(int diseaseId) {
        List<Map<String, String>> result = new ArrayList<>();
        for (Procedure prozedur : onkostarApi.getProceduresForDiseaseByForm(diseaseId, getFormName())) {
            prozedurToProzedurwerteMapper(prozedur).apply(prozedur).ifPresent(result::add);
        }
        return result;
    }

    /**
     * Übergibt aktuell immer den Mapper für das Formular "OS.Systemische Therapie",
     * da beide bekannte Varianten damit gemappt werden können.
     *
     * @param procedure Die Prozedur für die ein Mapper erstellt werden soll
     * @return Der Mapper für die Prozedur
     */
    @Override
    public ProzedurToProzedurwerteMapper prozedurToProzedurwerteMapper(Procedure procedure) {
        return new OsSystemischeTherapieToProzedurwerteMapper();
    }

    private String getFormName() {
        return settingsService
                .getSetting("systemtherapieform")
                .orElse("OS.Systemische Therapie");
    }
}
