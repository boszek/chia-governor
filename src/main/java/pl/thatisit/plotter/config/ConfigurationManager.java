package pl.thatisit.plotter.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStreamReader;

public final class ConfigurationManager {
    private static ChiaConfig config;

    public static ChiaConfig get() {
        return getOrLoad();
    }

    private static ChiaConfig getOrLoad() {
        if (config != null) {
            return config;
        }
        Yaml yamlParser = new Yaml();
        config = yamlParser.loadAs(new InputStreamReader(ConfigurationManager.class.getClassLoader().getResourceAsStream("configuration.yaml")), Configuration.class)
                    .getChia();
        return config;
    }
}
