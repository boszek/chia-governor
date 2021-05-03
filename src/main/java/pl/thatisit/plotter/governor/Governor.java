package pl.thatisit.plotter.governor;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.config.ConfigurationManager;
import pl.thatisit.plotter.domain.PlotStatus;
import pl.thatisit.plotter.domain.PlotterProcess;
import pl.thatisit.plotter.logprocessor.ProcessLogParser;
import pl.thatisit.plotter.runner.PlotProcessRunner;
import pl.thatisit.plotter.systemtask.SystemTaskProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.thatisit.plotter.domain.DateUUID.randomDateUUID;
import static pl.thatisit.plotter.domain.K.K_32;
import static pl.thatisit.plotter.drivespace.Drives.getDrive;

public class Governor {

    private final SystemTaskProvider systemTasks;
    private final ProcessLogParser processLogParser;
    private final ChiaConfig config = ConfigurationManager.get();
    private final SpaceGovernor spaceGovernor;
    private final PlotProcessRunner plotProcessRunner;

    public Governor(SystemTaskProvider systemTasks, ProcessLogParser processLogParser, PlotProcessRunner plotProcessRunner) {
        this.systemTasks = systemTasks;
        this.processLogParser = processLogParser;
        this.plotProcessRunner = plotProcessRunner;
        spaceGovernor = new SpaceGovernor(this);
    }

    List<PlotterProcess> managedTasks;
    List<PlotterProcess> unmanagedTasks;
    List<PlotterProcess> processes;

    public void init() {
        while (true) {
            loop();
        }
    }

    public void loop() {
        queryProcesses();
        parseLogs();
        printProcesses();
        planProcesses();
        sleep10s();
    }

    private void planProcesses() {
        config.getTemps()
                .stream()
                .filter(this::canStartPlotter)
                .forEach(this::startPlotter);
    }

    private void startPlotter(String temp) {
        var target = findTarget();
        plotProcessRunner.startProcess(temp, randomDateUUID(), target);
    }

    private String findTarget() {
        return config.getTargets()
                .stream()
                .filter(target -> spaceGovernor.diskInfo(target).getUsableFreeSpace() > K_32.getRequiredTargetSpace())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No space on target devices!"));
    }

    private boolean canStartPlotter(String temp) {
        var disk = spaceGovernor.diskInfo(temp);
        if (disk.getUsableFreeSpace() < K_32.getRequiredTempSpace()) {
            return false;
        }
        return !otherStage1Running(temp);
    }

    private boolean otherStage1Running(String temp) {
        var drive = getDrive(temp);
        return managedTasks.stream()
                .filter(task -> getDrive(task.getTempDrive()).equals(drive))
                .anyMatch(task -> PlotStatus.STAGE1.equals(task.getStatus()));
    }

    private void printProcesses() {
        System.out.printf("Processes: %d, managed: %d, unmanaged %d%n", processes.size(), managedTasks.size(), unmanagedTasks.size());
        managedTasks.forEach(process -> System.out.printf("Process %s, started %s, status %s%n",
                process.getId(), process.getStarted(), process.getStatus()));
        config.getTemps()
                .forEach(temp -> {
                    var disk = spaceGovernor.diskInfo(temp);
                    var msg = String.format("Temp %s: total=%s, allocated=%s, free=%s, usablefree=%s", temp,
                            toGB(disk.getTotalSize()),
                            toGB(disk.getAllocated()),
                            toGB(disk.getFreeSpace()),
                            toGB(disk.getUsableFreeSpace()));
                    System.out.println(msg);
                });
    }

    private String toGB(long value) {
        return (value / (1 << 30)) + "GB";
    }

    private void parseLogs() {
        managedTasks = managedTasks.stream()
                .map(processLogParser::init)
                .collect(Collectors.toList());
    }

    private void sleep10s() {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void queryProcesses() {
        processes = systemTasks.plotterProcesses();
        managedTasks = managed(processes)
                .map(this::processStatus)
                .collect(Collectors.toList());
        unmanagedTasks = unmanaged(processes).collect(Collectors.toList());
    }

    private PlotterProcess processStatus(PlotterProcess source) {
        return processLogParser.evaluateStatus(source);
    }

    private Stream<PlotterProcess> managed(List<PlotterProcess> src) {
        return src.stream().filter(PlotterProcess::isManaged);
    }

    private Stream<PlotterProcess> unmanaged(List<PlotterProcess> src) {
        return src.stream().filter(process -> !process.isManaged());
    }
}

