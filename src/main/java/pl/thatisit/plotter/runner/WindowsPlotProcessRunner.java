package pl.thatisit.plotter.runner;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.domain.DateUUID;
import pl.thatisit.plotter.domain.PlotterProcess;
import pl.thatisit.plotter.drivespace.Drives;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WindowsPlotProcessRunner implements PlotProcessRunner {

    private final String executable;
    private final String logsLocation;
    private final String memory;
    private final String keyId;
    private final int threads;

    public WindowsPlotProcessRunner(ChiaConfig chiaConfig, Drives drives) {
        logsLocation = chiaConfig.getLogs();
        memory = chiaConfig.getMemory();
        threads = chiaConfig.getThreads();
        keyId = chiaConfig.getKeyId();
        if (chiaConfig.getExecutable().endsWith("chia.exe")) {
            executable = chiaConfig.getExecutable();
        } else {
            executable = drives.findFile(chiaConfig.getExecutable(), "chia.exe").getAbsolutePath();
        }
    }

    @Override
    public PlotterProcess startProcess(String tempDrive, DateUUID jobId, String target) {
        try {
            final var temp = tempDrive + "\\" + jobId;
            final var logLocation = logsLocation + "\\" + jobId + ".log";
            Files.createDirectories(Path.of(temp));
            Files.createDirectories(Path.of(logsLocation));

            String cmd = String.format(executable +
                    " plots create -k32 -n1 -t%s -2%s \"-d%s\" -b%s -u128 -r%s -a%s", temp, temp, target, memory, threads, keyId);
            System.out.println("Starting process with the command: " + cmd);
            cmd = String.format("start /B %s > %s", cmd, logLocation);
            new ProcessBuilder()
                    .command("cmd.exe", "/c", cmd)
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
