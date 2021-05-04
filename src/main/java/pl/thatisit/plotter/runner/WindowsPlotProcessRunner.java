package pl.thatisit.plotter.runner;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.domain.DateUUID;
import pl.thatisit.plotter.domain.PlotterProcess;
import pl.thatisit.plotter.drivespace.Drives;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class WindowsPlotProcessRunner implements PlotProcessRunner{

    private final String executable;
    private final String logsLocation;
    private final String memory;

    public WindowsPlotProcessRunner(ChiaConfig chiaConfig) {
        logsLocation = chiaConfig.getLogs();
        memory = chiaConfig.getMemory();
        if(chiaConfig.getExecutable().endsWith("chia.exe")) {
            executable = chiaConfig.getExecutable();
        } else {
            executable = Drives.getInstance().findFile(chiaConfig.getExecutable(), "chia.exe").getAbsolutePath();
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
                    " plots create -k32 -n1 -t%s -2%s \"-d%s\" -b%s -u128", temp, temp, target, memory);
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