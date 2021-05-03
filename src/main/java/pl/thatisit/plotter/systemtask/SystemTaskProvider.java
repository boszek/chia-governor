package pl.thatisit.plotter.systemtask;

import pl.thatisit.plotter.domain.PlotterProcess;

import java.util.List;

public interface SystemTaskProvider {
    List<PlotterProcess> plotterProcesses();
}
