package DNPM.analyzer;

import de.itc.onkostar.api.analysis.IProcedureAnalyzer;

public interface IPluginPart extends IProcedureAnalyzer {

    default String getVersion() {
        return "0.4.0";
    }

    default String getName() {
        return "DNPM Plugin";
    }

    default String getDescription() {
        return String.format("Plugin-Bestandteil '%s'", this.getClass().getSimpleName());
    }

}
