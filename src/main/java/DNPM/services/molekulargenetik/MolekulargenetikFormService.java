package DNPM.services.molekulargenetik;

import DNPM.dto.Variant;
import de.itc.onkostar.api.Procedure;

import java.util.List;

public interface MolekulargenetikFormService {

    List<Variant> getVariants(Procedure procedure);

}
