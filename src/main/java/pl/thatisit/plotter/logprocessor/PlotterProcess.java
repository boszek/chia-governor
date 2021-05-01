package pl.thatisit.plotter.logprocessor;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlotterProcess {
    String id;
    int pid;
}
