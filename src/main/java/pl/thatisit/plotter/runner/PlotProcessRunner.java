package pl.thatisit.plotter.runner;

import pl.thatisit.plotter.DateUUID;
import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.PlotterProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlotProcessRunner {

    private final ChiaConfig config;

    public PlotProcessRunner(ChiaConfig chiaConfig) {
        this.config = chiaConfig;
    }

    public PlotterProcess startProcess(String tempDrive, DateUUID jobId, String target) {
        final var baseDir = System.getProperty("user.dir");

        try {
            final var temp = tempDrive + "\\" + jobId;
            final var logLocation = config.getLogs() + "\\" + jobId + ".log";
            Files.createDirectories(Path.of(temp));
            Files.createDirectories(Path.of(config.getLogs()));

            String cmd = String.format(config.getExecutable() +
                    " plots create -k32 -n1 -t%s -2%s \"-d%s\" -b%s -u128", temp, temp, target, config.getMemory());
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
