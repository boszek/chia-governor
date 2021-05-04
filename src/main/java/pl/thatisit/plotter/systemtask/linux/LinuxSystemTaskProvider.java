package pl.thatisit.plotter.systemtask.linux;

import org.apache.commons.lang3.StringUtils;
import pl.thatisit.plotter.domain.K;
import pl.thatisit.plotter.domain.PlotterProcess;
import pl.thatisit.plotter.drivespace.Drives;
import pl.thatisit.plotter.systemtask.Arguments;
import pl.thatisit.plotter.systemtask.SystemTaskProvider;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinuxSystemTaskProvider implements SystemTaskProvider {
    private static Pattern datePattern = Pattern.compile("(\\d{8}-\\d{6})");
    private final LinuxProcessCsvProvider linuxProcessCsvProvider;
    private final Drives drives;
    public LinuxSystemTaskProvider() {
        this(new LinuxProcessCsvProvider(), Drives.getInstance());
    }

    LinuxSystemTaskProvider(LinuxProcessCsvProvider linuxProcessCsvProvider, Drives drives) {
        this.linuxProcessCsvProvider = linuxProcessCsvProvider;
        this.drives = drives;
    }

    @Override
    public List<PlotterProcess> plotterProcesses() {
        return linuxProcessCsvProvider.getProcessesCsv()
                .stream()
                .filter(StringUtils::isNotEmpty)
                .filter(line -> line.contains("plots create") && line.contains("python"))
                .filter(line -> !line.contains("sh -c"))
                .map(this::toProcess)
                .collect(Collectors.toList());
    }

    private PlotterProcess toProcess(String line) {
        var csvLine = line.split(",");
        var cmd = Arguments.of(csvLine[1]);
        int pid = Integer.parseInt(csvLine[0].trim());
        return PlotterProcess.builder()
                .pid(pid)
                .id(id(cmd))
                .tempDrive(tempDrive(cmd))
                .targetDrive(targetDrive(cmd))
                .started(toDate(cmd))
                .k(k(cmd))
                .build();
    }

    private K k(Arguments command) {
        if (!command.contains("k")) {
            return null;
        }
        return K.of(command.get("k"));
    }

    private LocalDateTime toDate(Arguments cmd) {
        var dateTime = cmd.get("t");
        if (dateTime == null) {
            return null;
        }
        var matcher = datePattern.matcher(dateTime);
        if (matcher.find()) {
            return LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        }
        return null;
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
        return drives.getDrive(command.get("t"));
    }

    private String targetDrive(Arguments command) {
        if (!command.contains("d")) {
            return null;
        }
        return drives.getDrive(command.get("d"));
    }
}
