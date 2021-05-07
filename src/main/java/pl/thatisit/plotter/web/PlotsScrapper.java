package pl.thatisit.plotter.web;

import org.apache.commons.io.FileUtils;
import pl.thatisit.plotter.config.ChiaConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlotsScrapper {
    private final ChiaConfig config;

    public PlotsScrapper(ChiaConfig config) {
        this.config = config;
    }

    public List<String> findPlotFiles() {
        return config.getTargets()
                .stream()
                .flatMap(this::streamPlotFiles)
                .collect(Collectors.toList());
    }

    private Stream<String> streamPlotFiles(String location) {
        return FileUtils.listFiles(new File(location), new String[]{"plot"}, true)
                .stream()
                .map(file -> ((File)file).getName())
                .distinct();
    }
}
