package DNPM.analyzer;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.analysis.OnkostarPluginType;

public abstract class BackendService implements IPluginPart {

    @Override
    public final OnkostarPluginType getType() {
        return OnkostarPluginType.BACKEND_SERVICE;
    }

    /**
     * Ein Backend-Service verwendet die Methode nicht, daher wird hier eine final Stub-Implementierung
     * verwendet, die ein Überschreiben verhindert.
     * @param procedure
     * @param disease
     */
    @Override
    public final void analyze(Procedure procedure, Disease disease) {
        // No op
    }

}
