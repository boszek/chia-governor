package pl.thatisit.plotter.logprocessor;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Value
@Builder
public class StageProgress {
    Integer table;
    Integer bucket;
    String finished;

    @Override
    public String toString() {
        var values = new ArrayList<String>();
        if(finished != null ) {
             values.add("finished in: " + finished);
        } else {
            if (table != null && table > 0) {
                values.add("table: " + table);
            }
            if (bucket != null && bucket > 0) {
                values.add("bucket: " + bucket);
            }
        }
        return String.join(" ", values);
    }

    public boolean notEmpty() {
        return table != null || bucket != null || finished != null;
    }
}
