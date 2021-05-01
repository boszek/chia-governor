package pl.thatisit.plotter.governor;

import pl.thatisit.plotter.PlotterProcess;
import pl.thatisit.plotter.logprocessor.ProcessLogParser;
import pl.thatisit.plotter.systemtask.SystemTaskProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Governor {

    private final SystemTaskProvider systemTasks;
    private final ProcessLogParser processLogParser;

    public Governor(SystemTaskProvider systemTasks, ProcessLogParser processLogParser) {
        this.systemTasks = systemTasks;
        this.processLogParser = processLogParser;
    }

    List<PlotterProcess> tasks;

    public void init() {
        var tasks = systemTasks.plotterProcesses();
        this.tasks = managed(tasks)
                .map(this::processStatus)
                .collect(Collectors.toList());

        unmanaged(tasks)
                .forEach(process -> System.out.println("Non managed processes: " + process.toString()));
    }

    public void loop() {

    }

    private PlotterProcess processStatus(PlotterProcess source) {
        return processLogParser.parse(source);
    }

    private Stream<PlotterProcess> managed(List<PlotterProcess> src) {
        return src.stream().filter(PlotterProcess::isManaged);
    }

    private Stream<PlotterProcess> unmanaged(List<PlotterProcess> src) {
        return src.stream().filter(process -> !process.isManaged());
    }
}

