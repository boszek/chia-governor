package pl.thatisit.plotter;

import pl.thatisit.plotter.config.ConfigurationManager;
import pl.thatisit.plotter.governor.Governor;
import pl.thatisit.plotter.logprocessor.LogLoader;
import pl.thatisit.plotter.logprocessor.ProcessLogParser;
import pl.thatisit.plotter.runner.PlotProcessRunner;
import pl.thatisit.plotter.systemtask.WindowsSystemTaskProvider;

public class Main {

    public static void main(String[] args) {
        var chiaConfig = ConfigurationManager.get();

        var systemTaskProvider = new WindowsSystemTaskProvider();
        ProcessLogParser.init(new LogLoader(chiaConfig));
        var plotProcessRunner = new PlotProcessRunner(chiaConfig);
        var governor = new Governor(systemTaskProvider, plotProcessRunner);

        governor.init();
    }
}
