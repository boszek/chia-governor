package pl.thatisit.plotter.logprocessor;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.File;
import java.nio.file.Path;

public class LogLoader {
    private final ChiaConfig config;

    public LogLoader(ChiaConfig config) {
        this.config = config;
    }

    public File getLogFile(PlotterProcess process) {
        return Path.of(config.getLogs(), process.getId() + ".log").toFile();
    }
}
