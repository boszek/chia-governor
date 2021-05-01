package pl.thatisit.plotter;

import lombok.Builder;
import lombok.Value;

import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class PlotterProcess {
    String id;
    int pid;
    String tempDrive;
    String targetDrive;
    PlotStatus status;
    InputStreamReader logStream;
    LocalDateTime started;

    public boolean isManaged() {
        return DateUUID.isDateUUID(id);
    }
}