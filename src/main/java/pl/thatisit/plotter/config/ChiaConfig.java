package pl.thatisit.plotter.config;

import lombok.Data;

import java.util.List;

@Data
public class ChiaConfig {
    String python;
    String executable;
    String memory;
    int threads;
    String logs;
    List<Temp> temps;
    List<String> targets;
}
