package pl.thatisit.plotter.runner;

import pl.thatisit.plotter.domain.DateUUID;
import pl.thatisit.plotter.domain.PlotterProcess;

public interface PlotProcessRunner {
    PlotterProcess startProcess(String tempDrive, DateUUID jobId, String target);
}
