package pl.thatisit.plotter;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.config.ConfigurationManager;
import pl.thatisit.plotter.drivespace.LinuxDrives;
import pl.thatisit.plotter.drivespace.WindowsDrives;
import pl.thatisit.plotter.governor.Governor;
import pl.thatisit.plotter.logprocessor.LogLoader;
import pl.thatisit.plotter.logprocessor.ProcessLogParser;
import pl.thatisit.plotter.runner.LinuxPlotProcessRunner;
import pl.thatisit.plotter.runner.WindowsPlotProcessRunner;
import pl.thatisit.plotter.systemtask.linux.LinuxProcessCsvProvider;
import pl.thatisit.plotter.systemtask.linux.LinuxSystemTaskProvider;
import pl.thatisit.plotter.systemtask.windows.WindowsSystemTaskProvider;

public class Main {

    public static void main(String[] args) {
        Governor governor;
        var system = System.getProperty("os.name");
        if (system.contains("Linux")) {
            linux(ConfigurationManager.get("configuration-linux.yaml")).init();
        } else {
            windows(ConfigurationManager.get("configuration-windows.yaml")).init();
        }
    }

    private static Governor linux(ChiaConfig config) {
        var drives = new LinuxDrives();
        var systemTaskProvider = new LinuxSystemTaskProvider(new LinuxProcessCsvProvider(), drives);
        ProcessLogParser.init(new LogLoader(config));
        var plotProcessRunner = new LinuxPlotProcessRunner(config);
        return new Governor(systemTaskProvider, config, plotProcessRunner, drives);
    }

    private static Governor windows(ChiaConfig config) {
        var systemTaskProvider = new WindowsSystemTaskProvider();
        var drives = new WindowsDrives();
        ProcessLogParser.init(new LogLoader(config));
        var plotProcessRunner = new WindowsPlotProcessRunner(config, drives);
        return new Governor(systemTaskProvider, config, plotProcessRunner, drives);
    }
}
