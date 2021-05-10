package pl.thatisit.plotter.web.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class DailyPlotStatsDto {
    LocalDate date;
    int count;
}
