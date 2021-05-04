package pl.thatisit.plotter;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.config.ConfigurationManager;
import pl.thatisit.plotter.drivespace.Drives;
import pl.thatisit.plotter.governor.Governor;
import pl.thatisit.plotter.logprocessor.LogLoader;
import pl.thatisit.plotter.logprocessor.ProcessLogParser;
import pl.thatisit.plotter.runner.LinuxPlotProcessRunner;
import pl.thatisit.plotter.runner.WindowsPlotProcessRunner;
import pl.thatisit.plotter.systemtask.LinuxSystemTaskProvider;
import pl.thatisit.plotter.systemtask.WindowsSystemTaskProvider;

public class Main {

    public static void main(String[] args) {
        Governor governor;
        var system = System.getProperty("os.name");
        if(system.contains("Linux")) {
            governor = linux(ConfigurationManager.get("configuration-linux.yaml"));
        } else {
            governor = windows(ConfigurationManager.get("configuration-windows.yaml"));
        }
        governor.init();
    }

    private static Governor linux(ChiaConfig config) {
        var systemTaskProvider = new LinuxSystemTaskProvider();
        ProcessLogParser.init(new LogLoader(config));
        var plotProcessRunner = new LinuxPlotProcessRunner(config);
        return new Governor(systemTaskProvider, config, plotProcessRunner, Drives.getInstance());
    }

    private static Governor windows(ChiaConfig config) {
        var systemTaskProvider = new WindowsSystemTaskProvider();
        ProcessLogParser.init(new LogLoader(config));
        var plotProcessRunner = new WindowsPlotProcessRunner(config);
        return new Governor(systemTaskProvider, config, plotProcessRunner, Drives.getInstance());
    }
}
