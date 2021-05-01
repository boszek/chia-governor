package pl.thatisit.plotter.logprocessor;

import pl.thatisit.plotter.PlotterProcess;

public class ProcessLogParser {

    private final LogLoader logLoader;

    public ProcessLogParser(LogLoader logLoader) {
        this.logLoader = logLoader;
    }

    public PlotterProcess parse(PlotterProcess process) {
        if(!process.isManaged()) {
            return process;
        }
        var id = process.getId();

        return process;
    }
}
