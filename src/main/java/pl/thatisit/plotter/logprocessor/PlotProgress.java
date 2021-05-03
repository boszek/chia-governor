package pl.thatisit.plotter.logprocessor;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder
public class PlotProgress {
    PlotStatus status;
    String stage1;
    String stage2;
    String stage3;
    String stage4;

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if(StringUtils.isNotEmpty(stage1)) {
            sb.append("Stage1 ").append(stage1).append("; ");
        }
        if(StringUtils.isNotEmpty(stage2)) {
            sb.append("Stage2 ").append(stage2).append("; ");
        }
        if(StringUtils.isNotEmpty(stage3)) {
            sb.append("Stage3 ").append(stage3).append("; ");
        }
        if(StringUtils.isNotEmpty(stage4)) {
            sb.append("Stage4 ").append(stage4).append("; ");
        }
        return sb.toString();
    }
}
