package DNPM.analyzer;

import de.itc.onkostar.api.analysis.OnkostarPluginType;

public abstract class Analyzer implements IPluginPart {

    @Override
    public final OnkostarPluginType getType() {
        return OnkostarPluginType.ANALYZER;
    }

}
