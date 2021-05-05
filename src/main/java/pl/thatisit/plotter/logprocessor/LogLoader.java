package pl.thatisit.plotter.logprocessor;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class LogLoader {
    private final ChiaConfig config;

    public LogLoader(ChiaConfig config) {
        this.config = config;
    }

    public InputStreamReader getLogStream(PlotterProcess process) {
        try {
            return new InputStreamReader(new FileInputStream(Path.of(config.getLogs(), process.getId() + ".log").toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
