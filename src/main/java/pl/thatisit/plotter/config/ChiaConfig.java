package pl.thatisit.plotter.config;

import lombok.Data;

import java.util.List;

@Data
public class ChiaConfig {
    String executable;
    String memory;
    int threads;
    String logs;
    String keyId;
    List<Temp> temps;
    List<String> targets;
}
