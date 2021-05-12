package pl.thatisit.plotter.governor;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.config.Temp;
import pl.thatisit.plotter.domain.PlotterProcess;
import pl.thatisit.plotter.drivespace.Drives;
import pl.thatisit.plotter.logprocessor.LogProcessorManager;
import pl.thatisit.plotter.logprocessor.PlotStatus;
import pl.thatisit.plotter.logprocessor.ProcessLogParser;
import pl.thatisit.plotter.runner.PlotProcessRunner;
import pl.thatisit.plotter.systemtask.SystemTaskProvider;
import pl.thatisit.plotter.web.PlotsScrapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.thatisit.plotter.domain.DateUUID.randomDateUUID;
import static pl.thatisit.plotter.domain.K.K_32;
import static pl.thatisit.plotter.metrics.Metrics.registry;

public class Governor {

    private final SystemTaskProvider systemTasks;
    private final ChiaConfig config;
    private final SpaceGovernor spaceGovernor;
    private final PlotProcessRunner plotProcessRunner;
    private final PlotsScrapper plotsScrapper;
    private final Drives drives;
    private final LogProcessorManager logManager;

    public Governor(SystemTaskProvider systemTasks, ChiaConfig config, PlotProcessRunner plotProcessRunner, Drives drives, LogProcessorManager logManager) {
        this.systemTasks = systemTasks;
        this.config = config;
        this.plotProcessRunner = plotProcessRunner;
        this.drives = drives;
        this.logManager = logManager;
        spaceGovernor = new SpaceGovernor(this, drives);
        plotsScrapper = new PlotsScrapper(config);
        registry().gauge("plotter_plots", plots, AtomicInteger::get);
        registry().gauge("plotter_managedTasks", this, Governor::managedTasksCount);
        registry().gauge("plotter_unmanagedTasks", this, Governor::unmanagedTasksCount);
    }

    List<PlotterProcess> managedTasks = new ArrayList<>();
    List<PlotterProcess> unmanagedTasks = new ArrayList<>();
    List<PlotterProcess> processes;
    Map<String, ProcessLogParser> logParsers = new HashMap<>();
    AtomicInteger plots = new AtomicInteger(0);

    public Governor init() {
        queryProcesses();
        new Thread(() -> {
            while (true) {
                loop();
                sleep(30);
            }
        }).start();
        return this;
    }

    public void loop() {
        queryProcesses();
        printProcesses();
        planProcesses();
        countPlots();
        clearFinishedProcesses();
    }

    private void clearFinishedProcesses() {
        var loggers = new HashSet<>(logParsers.keySet());
        managedTasks.forEach(process -> loggers.remove(process.getId()));

        loggers.forEach(logger -> {
            logParsers.get(logger).close();
            logParsers.remove(logger);
        });
    }

    private void countPlots() {
        plots.set(plotsScrapper.findPlotFiles().size());
    }

    public List<PlotterProcess> processes() {
        return new ArrayList<>(managedTasks);
    }

    private void planProcesses() {
        config.getTemps()
                .stream()
                .filter(this::canStartPlotter)
                .map(Temp::getLocation)
                .forEach(this::startPlotter);
    }

    private void startPlotter(String temp) {
        var target = findTarget();
        if (target.isPresent()) {
            plotProcessRunner.startProcess(temp, randomDateUUID(), target.get());
        } else {
            System.out.println("No targets with free space available!");
        }
    }

    private Optional<String> findTarget() {
        return config.getTargets()
                .stream()
                .filter(target -> spaceGovernor.diskInfo(target, true).getUsableFreeSpace() > K_32.getRequiredTargetSpace())
                .findFirst();
    }

    private boolean canStartPlotter(Temp temp) {
        var disk = spaceGovernor.diskInfo(temp.getLocation(), true);
        if (disk.getUsableFreeSpace() < K_32.getRequiredTempSpace()) {
            return false;
        }
        if (otherStage1Running(temp.getLocation()) && startedLessThan2hrsAgo()) {
            return false;
        }
        if (runningProcessesOn(temp.getLocation()) >= temp.getLimit()) {
            return false;
        }
        return true;
    }

    private boolean startedLessThan2hrsAgo() {
        return managedTasks.stream()
                .anyMatch(task -> task.getStarted().isAfter(LocalDateTime.now().minusHours(2)));
    }

    private int runningProcessesOn(String location) {
        var drive = drives.getDrive(location);
        return (int) managedTasks.stream()
                .filter(task -> drives.getDrive(task.getTempDrive()).equals(drive))
                .filter(task -> task.getStatus() != PlotStatus.FINISHED)
                .count();
    }

    private boolean otherStage1Running(String temp) {
        var drive = drives.getDrive(temp);
        return managedTasks.stream()
                .filter(task -> drives.getDrive(task.getTempDrive()).equals(drive))
                .anyMatch(task -> PlotStatus.STAGE1.equals(task.getStatus()));
    }

    private void printProcesses() {
        System.out.printf("Processes: %d, managed: %d, unmanaged %d%n", processes.size(), managedTasks.size(), unmanagedTasks.size());
        managedTasks.forEach(process -> System.out.printf("Process %s, started %s, temp %s, target: %s status %s, progress: %s\n",
                process.getId(), process.getStarted(), process.getTempDrive(), process.getTargetDrive(), process.getStatus(), process.getProgress()));
        config.getTemps().stream().map(Temp::getLocation).forEach(temp -> printDiskInfo(temp, true));
        config.getTargets().forEach(target -> printDiskInfo(target, false));
    }

    private void printDiskInfo(String location, boolean includeTemps) {
        var disk = spaceGovernor.diskInfo(location, includeTemps);
        var msg = String.format("%s: total=%s, allocated=%s, free=%s, usablefree=%s", location,
                toGB(disk.getTotalSize()),
                toGB(disk.getAllocated()),
                toGB(disk.getFreeSpace()),
                toGB(disk.getUsableFreeSpace()));
        System.out.println(msg);
    }

    private String toGB(long value) {
        return (value / (1 << 30)) + "GB";
    }

    private void sleep(int n) {
        try {
            Thread.sleep(n * 1000);
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
        return logManager.get(source).processLogs();
    }

    private Stream<PlotterProcess> managed(List<PlotterProcess> src) {
        return src.stream().filter(PlotterProcess::isManaged);
    }

    private Stream<PlotterProcess> unmanaged(List<PlotterProcess> src) {
        return src.stream().filter(process -> !process.isManaged());
    }

    private int managedTasksCount() {
        return managedTasks.size();
    }
    private int unmanagedTasksCount() {
        return unmanagedTasks.size();
    }
}

