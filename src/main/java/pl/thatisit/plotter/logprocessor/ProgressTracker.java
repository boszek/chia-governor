package pl.thatisit.plotter.logprocessor;

import lombok.Data;

@Data
class ProgressTracker {
    Integer stage1table;
    Integer stage1bucket;
    String stage1finished;
    Integer stage2table;
    String stage2finished;
    Integer stage3table;
    Integer stage3bucket;
    String stage3finished;
    Integer stage4bucket;
    String stage4finished;

    public PlotStatus toStatus() {
        if (stage4finished != null) {
            return PlotStatus.FINISHED;
        } else if (stage3finished != null) {
            return PlotStatus.STAGE4;
        } else if (stage2finished != null) {
            return PlotStatus.STAGE3;
        } else if (stage1finished != null) {
            return PlotStatus.STAGE2;
        } else {
            return PlotStatus.STAGE1;
        }
    }

    public PlotProgress toProgress() {
        return PlotProgress.builder()
                .status(toStatus())
                .stage1(format(stage1finished, stage1table, stage1bucket))
                .stage2(format(stage2finished, stage2table, null))
                .stage3(format(stage3finished, stage3table, stage3bucket))
                .stage4(format(stage4finished, null, stage4bucket))
                .build();
    }

    private String format(String finished, Integer table, Integer bucket) {
        if (finished == null && table == null && bucket == null) {
            return "";
        }
        var sb = new StringBuilder();
        if (finished != null) {
            sb.append("Complete in ").append(finished);
        } else {
            if (table != null) {
                sb.append(" Table: ").append(table);
            }
            if (bucket != null) {
                sb.append(" bucket: ").append(bucket);
            }
        }
        return sb.toString();
    }

}
