package pl.thatisit.plotter.web;

import lombok.Value;

import java.time.LocalDate;

@Value
public class DailyPlotStatsDto {
    LocalDate date;
    int count;
}
