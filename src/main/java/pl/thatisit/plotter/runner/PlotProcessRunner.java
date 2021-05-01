package pl.thatisit.plotter.runner;

import pl.thatisit.plotter.DateUUID;
import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.logprocessor.PlotterProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlotProcessRunner {

    private final ChiaConfig config;

    public PlotProcessRunner(ChiaConfig chiaConfig) {
        this.config = chiaConfig;
    }

    public PlotterProcess startProcess(String tempDrive, DateUUID jobId) {
        final var baseDir = System.getProperty("user.dir");

        try {
            final var temp = tempDrive + "\\" + jobId;
            final var logLocation = config.getLogs() + "\\" + jobId + ".log";
            Files.createDirectories(Path.of(temp));
            Files.createDirectories(Path.of(config.getLogs()));

            String cmd = String.format(config.getExecutable() +
                    " plots create -k32 -n1 -t%s -2%s \"-dJ:\\Chia Plots\" -b3390 -u128", temp, temp);
            cmd = String.format("start echo 13", cmd, logLocation);
            final var process = Runtime.getRuntime().exec(cmd);
            System.out.println(process.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
