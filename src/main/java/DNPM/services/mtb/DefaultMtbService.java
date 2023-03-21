package DNPM.services.mtb;

import DNPM.services.SettingsService;
import de.itc.onkostar.api.Procedure;

import java.util.List;

public class DefaultMtbService implements MtbService {

    private final SettingsService settingsService;

    public DefaultMtbService(final SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Override
    public String getProtocol(List<Procedure> procedures) {
        ProcedureToProtocolMapper mapper = null;
        var sid = settingsService.getSID();

        if (sid.isPresent()) {
            switch (sid.get()) {
                case "2011":
                case "20119":
                    mapper = new OsTumorkonferenzVarianteUkwToProtocolMapper();
                default:
                    if (!settingsService.multipleMtbsInMtbEpisode()) {
                        mapper = new OsTumorkonferenzToProtocolMapper();
                    }
            }
        }

        if (null == mapper) {
            return "";
        }

        return mapper.apply(procedures).orElse("");
    }

}
