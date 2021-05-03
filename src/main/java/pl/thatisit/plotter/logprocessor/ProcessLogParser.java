package pl.thatisit.plotter.logprocessor;

import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessLogParser {

    private static final Pattern STAGE_1_FINISHED = Pattern.compile("Time for phase 1 = (\\d+).\\d+ seconds.*");
    private static final Pattern STAGE_1_TABLE = Pattern.compile("Computing table (\\d+).*");
    private static final Pattern STAGE_1_BUCKET = Pattern.compile("Bucket (\\d+) uniform sort.*");
    private static final Pattern STAGE_2_TABLE = Pattern.compile("Backpropagating on table (\\d+).*");
    private static final Pattern STAGE_2_FINISHED = Pattern.compile("Time for phase 2 = (\\d+).\\d+ seconds.*");
    private static final Pattern STAGE_3_TABLE = Pattern.compile("Compressing tables 1 and (\\d+)");
    private static final Pattern STAGE_3_BUCKET = Pattern.compile("Bucket (\\d+).*");
    private static final Pattern STAGE_3_FINISHED = Pattern.compile("Time for phase 3 = (\\d+).\\d+ seconds.*");
    private static final Pattern STAGE_4_BUCKET = Pattern.compile("Bucket (\\d+).*");
    private static final Pattern STAGE_4_FINISHED = Pattern.compile("Time for phase 4 = (\\d+).\\d+ seconds.*");

    private static LogLoader logLoader;
    public static void init(LogLoader loader) {
        logLoader = loader;
    }

    private ProcessLogParser() {
    }

    public static PlotterProcess evaluateStatus(PlotterProcess process) {
        return new ProcessLogParser().processLogs(process);
    }

    private ProgressTracker progressTracker = new ProgressTracker();
    int stage = 1;

    private PlotterProcess processLogs(PlotterProcess process) {
        try (var logStream = new BufferedReader(logLoader.getLogStream(process))) {
            String line;

            while ((line = logStream.readLine()) != null) {
                if(stage == 1) {
                    matches(STAGE_1_BUCKET, line).ifPresent(this::setStage1Bucket);
                    matches(STAGE_1_TABLE, line).ifPresent(this::setStage1Table);
                    matches(STAGE_1_FINISHED, line).ifPresent(this::setStage1Finished);
                }
                if(stage == 2) {
                    matches(STAGE_2_TABLE, line).ifPresent(this::setStage2Table);
                    matches(STAGE_2_FINISHED, line).ifPresent(this::setStage2Finished);
                }
                if(stage == 3) {
                    matches(STAGE_3_TABLE, line).ifPresent(this::setStage3Table);
                    matches(STAGE_3_BUCKET, line).ifPresent(this::setStage3Bucket);
                    matches(STAGE_3_FINISHED, line).ifPresent(this::setStage3Finished);
                }
                if(stage == 4) {
                    matches(STAGE_4_BUCKET, line).ifPresent(this::setStage4Bucket);
                    matches(STAGE_4_FINISHED, line).ifPresent(this::setStage4Finished);
                }
            }
            return process.toBuilder()
                    .status(progressTracker.toStatus())
                    .progress(progressTracker.toProgress())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process;
    }

    private void setStage4Bucket(Matcher matcher) {
        progressTracker.setStage4bucket(Integer.parseInt(matcher.group(1)));
    }

    private void setStage3Bucket(Matcher matcher) {
        progressTracker.setStage3bucket(Integer.parseInt(matcher.group(1)));
    }

    private void setStage3Table(Matcher matcher) {
        progressTracker.setStage3table(Integer.parseInt(matcher.group(1)));
    }

    private void setStage2Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        progressTracker.setStage2finished(formatSeconds(seconds));
        stage = 3;
    }

    private void setStage3Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        progressTracker.setStage3finished(formatSeconds(seconds));
        stage = 4;
    }

    private void setStage4Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        progressTracker.setStage4finished(formatSeconds(seconds));
        stage = 5;
    }

    private void setStage2Table(Matcher matcher) {
        progressTracker.setStage2table(8-Integer.parseInt(matcher.group(1)));
    }

    private void setStage1Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        progressTracker.setStage1finished(formatSeconds(seconds));
        stage = 2;
    }

    private void setStage1Table(Matcher m) {
        progressTracker.setStage1table(Integer.parseInt(m.group(1)));
    }

    private void setStage1Bucket(Matcher m) {
        progressTracker.setStage1bucket(Integer.parseInt(m.group(1)));
    }

    private Optional<Matcher> matches(Pattern pattern, String value) {
        var matcher = pattern.matcher(value);
        if(matcher.find()) {
            return Optional.of(matcher);
        }
        return Optional.empty();
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

    private String formatSeconds(int totalSeconds) {
        var hours = totalSeconds / 3600;
        var minutes = (totalSeconds % 3600) / 60;
        var seconds = totalSeconds % 60;
        return String.format("%sh%sm%ss",hours,minutes,seconds);
    }
}
