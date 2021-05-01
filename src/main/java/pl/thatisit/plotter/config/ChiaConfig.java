package pl.thatisit.plotter.config;

import lombok.Data;

import java.util.List;

@Data
public class ChiaConfig {
    String executable;
    String memory;
    String logs;
    List<String> temps;
    List<String> targets;
}
