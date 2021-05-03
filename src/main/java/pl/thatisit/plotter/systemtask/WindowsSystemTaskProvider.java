package pl.thatisit.plotter.systemtask;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import pl.thatisit.plotter.domain.K;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class WindowsSystemTaskProvider implements SystemTaskProvider {

    private final WindowsProcessCsvProvider windowsProcessCsvProvider;

    public WindowsSystemTaskProvider() {
        this(new WindowsProcessCsvProvider());
    }

    WindowsSystemTaskProvider(WindowsProcessCsvProvider windowsProcessCsvProvider) {
        this.windowsProcessCsvProvider = windowsProcessCsvProvider;
    }

    @Override
    public List<PlotterProcess> plotterProcesses() {
        try {
            return IOUtils.toString(windowsProcessCsvProvider.getProcessesCsv())
                    .lines()
                    .filter(StringUtils::isNotEmpty)
                    .filter(line -> line.contains("plots create"))
                    .map(this::toProcess)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private PlotterProcess toProcess(String line) {
        var csvLine = line.split(",");
        var cmd = Arguments.of(csvLine[1]);
        var date = csvLine[2];
        int pid = Integer.parseInt(csvLine[3]);
        return PlotterProcess.builder()
                .pid(pid)
                .id(id(cmd))
                .tempDrive(tempDrive(cmd))
                .targetDrive(targetDrive(cmd))
                .started(toDate(date))
                .k(k(cmd))
                .build();
    }

    private K k(Arguments command) {
        if(!command.contains("k")) {
            return null;
        }
        return K.of(command.get("k"));
    }

    private LocalDateTime toDate(String date) {
        return LocalDateTime.parse(date.split("\\.")[0], DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String id(Arguments command) {
        if (!command.contains("t")) {
            return null;
        }
        var tempPath = Path.of(command.get("t"));
        return tempPath.getFileName().toString();
    }

    private String tempDrive(Arguments command) {
        if (!command.contains("t")) {
            return null;
        }
        var tempPath = Path.of(command.get("t"));
        return getDrive(tempPath);
    }

    private String targetDrive(Arguments command) {
        if (!command.contains("d")) {
            return null;
        }
        var tempPath = Path.of(command.get("d"));
        return getDrive(tempPath);
    }

    private String getDrive(Path path) {
        return path.getRoot().toString().replaceAll("[\\\\/]", "").toUpperCase();
    }

}
