package pl.thatisit.plotter.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlotsStatistics {

    private final Pattern pattern = Pattern.compile("^plot-(k\\d+)-(\\d{4}-\\d{2}-\\d{2})-\\d{2}-\\d{2}-[a-f0-9]{64}\\.plot$");
    //private final
    private final PlotsScrapper plotsScrapper;

    public PlotsStatistics(PlotsScrapper plotsScrapper) {
        this.plotsScrapper = plotsScrapper;
    }

    public List<DailyPlotStatsDto> get() {
        return plotsScrapper.findPlotFiles()
                .stream()
                .map(this::toDate)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity()))
                .entrySet()
                .stream()
                .map(entry -> new DailyPlotStatsDto(entry.getKey(), entry.getValue().size()))
                .sorted(Comparator.comparing(DailyPlotStatsDto::getDate))
                .collect(Collectors.toList());

    }

    private LocalDate toDate(String s) {
        var matcher = pattern.matcher(s);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return null;
    }
}
