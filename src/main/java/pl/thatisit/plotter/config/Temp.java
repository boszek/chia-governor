package pl.thatisit.plotter.config;

import lombok.Data;

@Data
public class Temp {
    String location;
    int limit = Integer.MAX_VALUE;
}
