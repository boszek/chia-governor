package pl.thatisit.plotter.logprocessor.systemtask;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import pl.thatisit.plotter.logprocessor.PlotterProcess;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class WindowsSystemTaskProvider implements SystemTaskProvider {

    private final WindowsProcessCsvProvider windowsProcessCsvProvider;

    public WindowsSystemTaskProvider(WindowsProcessCsvProvider windowsProcessCsvProvider) {
        this.windowsProcessCsvProvider = windowsProcessCsvProvider;
    }

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
        var cmd = csvLine[1];
        int pid = Integer.parseInt(csvLine[2]);
        return PlotterProcess.builder()
                .pid(pid)
                .id(id(cmd))
                .build();
    }

    private String id(String command) {
        if (!command.contains("-t")) {
            return null;
        }
        var tempLocation = command.substring(command.indexOf("chia.exe"));
        tempLocation = tempLocation.substring(tempLocation.indexOf("-t") + 2);
        tempLocation = tempLocation.substring(0, tempLocation.indexOf(" "));
        tempLocation = tempLocation.substring(tempLocation.lastIndexOf("\\") + 1);
        return tempLocation;
    }

}
