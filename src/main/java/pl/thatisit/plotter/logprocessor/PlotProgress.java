package pl.thatisit.plotter.logprocessor;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlotProgress {
    PlotStatus status;
    StageProgress stage1;
    StageProgress stage2;
    StageProgress stage3;
    StageProgress stage4;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if(stage1 != null && stage1.notEmpty()) {
            sb.append("Stage1 ").append(stage1).append("; ");
        }
        if(stage2 != null && stage2.notEmpty()) {
            sb.append("Stage2 ").append(stage2).append("; ");
        }
        if(stage3 != null && stage3.notEmpty()) {
            sb.append("Stage3 ").append(stage3).append("; ");
        }
        if(stage4 != null && stage4.notEmpty()) {
            sb.append("Stage4 ").append(stage4).append("; ");
        }
        return sb.toString();
    }
}
