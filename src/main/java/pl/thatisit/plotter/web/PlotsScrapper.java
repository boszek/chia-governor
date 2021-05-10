package pl.thatisit.plotter.web;

import org.apache.commons.io.FileUtils;
import pl.thatisit.plotter.config.ChiaConfig;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlotsScrapper {
    private final Pattern pattern = Pattern.compile("^plot-(k\\d+)-(\\d{4}-\\d{2}-\\d{2})-\\d{2}-\\d{2}-[a-f0-9]{64}\\.plot$");
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
        try {
            Collection<File> files = FileUtils.listFiles(new File(location), new String[]{"plot"}, true);
            return files
                    .stream()
                    .map(File::getName)
                    .filter(name -> pattern.matcher(name).find())
                    .distinct();
        } catch (Throwable e) {
            return Stream.empty();
        }
    }
}
