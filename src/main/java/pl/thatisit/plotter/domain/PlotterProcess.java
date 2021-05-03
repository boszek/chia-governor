package pl.thatisit.plotter.domain;

import lombok.Builder;
import lombok.Value;
import pl.thatisit.plotter.logprocessor.PlotProgress;
import pl.thatisit.plotter.logprocessor.PlotStatus;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class PlotterProcess {
    String id;
    int pid;
    String tempDrive;
    String targetDrive;
    PlotStatus status;
    PlotProgress progress;
    K k;
    LocalDateTime started;

    public boolean isManaged() {
        return DateUUID.isDateUUID(id);
    }
}
