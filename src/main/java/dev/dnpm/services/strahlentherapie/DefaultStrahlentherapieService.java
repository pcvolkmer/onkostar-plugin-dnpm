package dev.dnpm.services.strahlentherapie;

import dev.dnpm.dto.EcogStatusWithDate;
import dev.dnpm.services.SettingsService;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Standardimplementierung des StrahlentherapieServices
 *
 * @since 0.6.0
 */
public class DefaultStrahlentherapieService implements StrahlentherapieService {

    private static final String ECOG_FIELD = "ECOGvorTherapie";

    private final IOnkostarApi onkostarApi;

    private final SettingsService settingsService;

    public DefaultStrahlentherapieService(final IOnkostarApi onkostarApi, final SettingsService settingsService) {
        this.onkostarApi = onkostarApi;
        this.settingsService = settingsService;
    }

    /**
     * Ermittelt den letzten bekannten ECOG-Status aus allen Systemtherapieformularen des Patienten
     *
     * @param patient Der zu verwendende Patient
     * @return Der ECOG-Status als String oder leeres Optional
     */
    @Override
    public Optional<String> latestEcogStatus(Patient patient) {
        return ecogStatus(patient).stream()
                .max(Comparator.comparing(EcogStatusWithDate::getDate))
                .map(EcogStatusWithDate::getStatus);
    }

    /**
     * Ermittelt jeden bekannten ECOG-Status aus allen Systemtherapieformularen des Patienten
     *
     * @param patient Der zu verwendende Patient
     * @return Eine Liste mit Datum und ECOG-Status als String
     */
    @Override
    public List<EcogStatusWithDate> ecogStatus(Patient patient) {
        return patient.getDiseases().stream()
                .flatMap(disease -> onkostarApi.getProceduresForDiseaseByForm(disease.getId(), getFormName()).stream())
                .filter(procedure -> null != procedure.getStartDate())
                .sorted(Comparator.comparing(Procedure::getStartDate))
                .map(procedure -> {
                    try {
                        return new EcogStatusWithDate(procedure.getStartDate(), procedure.getValue(ECOG_FIELD).getString());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getFormName() {
        return settingsService
                .getSetting("strahlentherapieform")
                .orElse("OS.Strahlentherapie");
    }
}
