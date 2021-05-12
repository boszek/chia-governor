package pl.thatisit.plotter;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.config.ConfigurationManager;
import pl.thatisit.plotter.drivespace.LinuxDrives;
import pl.thatisit.plotter.drivespace.WindowsDrives;
import pl.thatisit.plotter.governor.Governor;
import pl.thatisit.plotter.logprocessor.LogLoader;
import pl.thatisit.plotter.logprocessor.LogProcessorManager;
import pl.thatisit.plotter.metrics.Metrics;
import pl.thatisit.plotter.runner.LinuxPlotProcessRunner;
import pl.thatisit.plotter.runner.WindowsPlotProcessRunner;
import pl.thatisit.plotter.systemtask.linux.LinuxProcessCsvProvider;
import pl.thatisit.plotter.systemtask.linux.LinuxSystemTaskProvider;
import pl.thatisit.plotter.systemtask.windows.WindowsSystemTaskProvider;

import static pl.thatisit.plotter.web.converter.Converter.toJson;
import static spark.Spark.get;

public class Main {

    public static void main(String[] args) {
        final var system = System.getProperty("os.name");
        final Governor governor;
        final ChiaConfig config;

        if (system.contains("Linux")) {
            config = ConfigurationManager.get("configuration-linux.yaml");
            var logManager = new LogProcessorManager(new LogLoader(config));
            governor = linux(config, logManager).init();
        } else {
            config = ConfigurationManager.get("configuration-windows.yaml");
            var logManager = new LogProcessorManager(new LogLoader(config));
            governor = windows(config, logManager).init();
        }
        configureWebServer(governor);
    }

    private static void configureWebServer(Governor governor) {
        get("/v1/processes", (req, res) -> toJson(governor.processes()));
        get("/prometheus", (req, res) -> Metrics.registry().scrape());
    }

    private static Governor linux(ChiaConfig config, LogProcessorManager logManager) {
        var drives = new LinuxDrives();
        var systemTaskProvider = new LinuxSystemTaskProvider(new LinuxProcessCsvProvider(), drives);
        var plotProcessRunner = new LinuxPlotProcessRunner(config);
        return new Governor(systemTaskProvider, config, plotProcessRunner, drives, logManager);
    }

    private static Governor windows(ChiaConfig config, LogProcessorManager logManager) {
        var systemTaskProvider = new WindowsSystemTaskProvider();
        var drives = new WindowsDrives();
        var plotProcessRunner = new WindowsPlotProcessRunner(config, drives);
        return new Governor(systemTaskProvider, config, plotProcessRunner, drives, logManager);
    }
}
