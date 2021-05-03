package pl.thatisit.plotter.logprocessor;

import pl.thatisit.plotter.domain.PlotStatus;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class ProcessLogParser {

    private static final Pattern PHASE_PATTERN = Pattern.compile("Time for phase (\\d+) = (\\d+).\\d+ seconds.*");

    private final LogLoader logLoader;

    public ProcessLogParser(LogLoader logLoader) {
        this.logLoader = logLoader;
    }

    public PlotterProcess evaluateStatus(PlotterProcess process) {
        try (var logStream = new BufferedReader(logLoader.getLogStream(process))) {
            String line;
            PlotStatus status = PlotStatus.STAGE1;
            while ((line = logStream.readLine()) != null) {
                var maybePhaseFinished = finishPhase(line);
                if(maybePhaseFinished.isPresent()) {
                    status = toStatus(maybePhaseFinished.get());
                }
            }
            return process.toBuilder().status(status).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    private PlotStatus toStatus(Integer phaseFinished) {
        switch(phaseFinished) {
            case 1: return PlotStatus.STAGE2;
            case 2: return PlotStatus.STAGE3;
            case 3: return PlotStatus.STAGE4;
            case 4: return PlotStatus.FINISHED;
            default: return PlotStatus.STAGE1;
        }
    }

    public PlotterProcess init(PlotterProcess process) {
        if (!process.isManaged()) {
            return process;
        }
        var id = process.getId();

        return process;
    }

    private Optional<Integer> finishPhase(String line) {
        var matcher = PHASE_PATTERN.matcher(line);
        if (matcher.find()) {
            var phase = matcher.group(1);
            return Optional.of(Integer.valueOf(phase));
        }
        return Optional.empty();
    }
}
