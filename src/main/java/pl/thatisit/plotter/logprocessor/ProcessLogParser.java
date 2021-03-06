package pl.thatisit.plotter.logprocessor;

import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pl.thatisit.plotter.metrics.Metrics.registry;

public class ProcessLogParser {

    private static final Pattern STAGE_1_FINISHED = Pattern.compile("Time for phase 1 = (\\d+).\\d+ seconds.*");
    private static final Pattern STAGE_1_TABLE = Pattern.compile("Computing table (\\d+).*");
    private static final Pattern STAGE_1_BUCKET = Pattern.compile("Bucket (\\d+) uniform sort.*");
    private static final Pattern STAGE_2_TABLE = Pattern.compile("Backpropagating on table (\\d+).*");
    private static final Pattern STAGE_2_FINISHED = Pattern.compile("Time for phase 2 = (\\d+).\\d+ seconds.*");
    private static final Pattern STAGE_3_TABLE = Pattern.compile("Compressing tables \\d and (\\d+)");
    private static final Pattern STAGE_3_BUCKET = Pattern.compile("Bucket (\\d+).*");
    private static final Pattern STAGE_3_FINISHED = Pattern.compile("Time for phase 3 = (\\d+).\\d+ seconds.*");
    private static final Pattern STAGE_4_BUCKET = Pattern.compile("Bucket (\\d+).*");
    private static final Pattern STAGE_4_FINISHED = Pattern.compile("Time for phase 4 = (\\d+).\\d+ seconds.*");
    private final PlotterProcess process;

    @Getter
    private Integer stage = 1;
    @Getter
    private Integer table = 0;
    @Getter
    private Integer bucket = 0;

    private StageProgress stage1;
    private StageProgress stage2;
    private StageProgress stage3;
    private StageProgress stage4;

    ProcessLogParser(PlotterProcess process) {
        this.process = process;
        registry().gauge("plotter_process_stage", List.of(Tag.of("id", process.getId())), this, ProcessLogParser::getStage);
        registry().gauge("plotter_process_table", List.of(Tag.of("id", process.getId())), this, ProcessLogParser::getTable);
        registry().gauge("plotter_process_bucket", List.of(Tag.of("id", process.getId())), this, ProcessLogParser::getBucket);
    }

    public void close() {
        stage = 6;
        bucket = 0;
        table = 0;
    }

    void processLine(String line) {
        if (stage == 1) {
            matches(STAGE_1_BUCKET, line).ifPresent(this::setStage1Bucket);
            matches(STAGE_1_TABLE, line).ifPresent(this::setStage1Table);
            matches(STAGE_1_FINISHED, line).ifPresent(this::setStage1Finished);
        }
        if (stage == 2) {
            matches(STAGE_2_TABLE, line).ifPresent(this::setStage2Table);
            matches(STAGE_2_FINISHED, line).ifPresent(this::setStage2Finished);
        }
        if (stage == 3) {
            matches(STAGE_3_TABLE, line).ifPresent(this::setStage3Table);
            matches(STAGE_3_BUCKET, line).ifPresent(this::setStage3Bucket);
            matches(STAGE_3_FINISHED, line).ifPresent(this::setStage3Finished);
        }
        if (stage == 4) {
            matches(STAGE_4_BUCKET, line).ifPresent(this::setStage4Bucket);
            matches(STAGE_4_FINISHED, line).ifPresent(this::setStage4Finished);
        }
    }

    public PlotterProcess processLogs() {
        var progress = toProgress(stage);
        return process.toBuilder()
                .status(progress.getStatus())
                .progress(progress)
                .build();
    }

    private void setStage1Table(Matcher m) {
        table = Integer.parseInt(m.group(1));
    }

    private void setStage1Bucket(Matcher m) {
        bucket = Integer.parseInt(m.group(1));
    }

    private void setStage1Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        stage1 = StageProgress.builder().finished(formatSeconds(seconds)).build();
        setStage(2);
    }

    private void setStage2Table(Matcher matcher) {
        table = 8 - Integer.parseInt(matcher.group(1));
    }

    private void setStage2Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        stage2 = StageProgress.builder().finished(formatSeconds(seconds)).build();
        setStage(3);
    }

    private void setStage3Bucket(Matcher matcher) {
        bucket = Integer.parseInt(matcher.group(1));
    }

    private void setStage3Table(Matcher matcher) {
        table = Integer.parseInt(matcher.group(1));
    }

    private void setStage3Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        stage3 = StageProgress.builder().finished(formatSeconds(seconds)).build();
        setStage(4);
    }

    private void setStage4Bucket(Matcher matcher) {
        bucket = Integer.parseInt(matcher.group(1));
    }

    private void setStage4Finished(Matcher matcher) {
        var seconds = Integer.parseInt(matcher.group(1));
        stage4 = StageProgress.builder().finished(formatSeconds(seconds)).build();
        setStage(5);
    }

    private Optional<Matcher> matches(Pattern pattern, String value) {
        var matcher = pattern.matcher(value);
        if (matcher.find()) {
            return Optional.of(matcher);
        }
        return Optional.empty();
    }

    private PlotProgress toProgress(Integer phaseFinished) {
        switch (phaseFinished) {
            case 2:
                return PlotProgress.builder().status(PlotStatus.STAGE2)
                        .stage1(stage1)
                        .stage2(StageProgress.builder().table(table).bucket(bucket).build()).build();
            case 3:
                return PlotProgress.builder().status(PlotStatus.STAGE3)
                        .stage1(stage1)
                        .stage2(stage2)
                        .stage3(StageProgress.builder().table(table).bucket(bucket).build()).build();
            case 4:
                return PlotProgress.builder().status(PlotStatus.STAGE4)
                        .stage1(stage1)
                        .stage2(stage2)
                        .stage3(stage3)
                        .stage4(StageProgress.builder().table(table).bucket(bucket).build()).build();
            case 5:
                return PlotProgress.builder()
                        .stage1(stage1)
                        .stage2(stage2)
                        .stage3(stage3)
                        .stage4(stage4)
                        .status(PlotStatus.FINISHED).build();
            default:
                return PlotProgress.builder().status(PlotStatus.STAGE1)
                        .stage1(StageProgress.builder().table(table).bucket(bucket).build()).build();
        }
    }

    private void setStage(int i) {
        stage = i;
        bucket = 0;
        table = 0;
    }

    private String formatSeconds(int totalSeconds) {
        var hours = totalSeconds / 3600;
        var minutes = (totalSeconds % 3600) / 60;
        var seconds = totalSeconds % 60;
        return String.format("%sh%sm%ss", hours, minutes, seconds);
    }
}
