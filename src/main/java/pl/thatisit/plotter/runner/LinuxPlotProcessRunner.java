package pl.thatisit.plotter.runner;

import pl.thatisit.plotter.config.ChiaConfig;
import pl.thatisit.plotter.domain.DateUUID;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinuxPlotProcessRunner implements PlotProcessRunner {

    private final String executable;
    private final String python;
    private final String logsLocation;
    private final String memory;

    public LinuxPlotProcessRunner(ChiaConfig chiaConfig) {
        this.logsLocation = chiaConfig.getLogs();
        this.memory = chiaConfig.getMemory();
        this.executable = chiaConfig.getExecutable();
        this.python = chiaConfig.getPython();

    }

    @Override
    public PlotterProcess startProcess(String tempDrive, DateUUID jobId, String target) {
        try {
            final var temp = tempDrive + "/" + jobId;
            final var logLocation = logsLocation + "/" + jobId + ".log";
            Files.createDirectories(Path.of(temp));
            Files.createDirectories(Path.of(logsLocation));

            String cmd = String.format(python + " " + executable +
                    " plots create -k32 -n1 \"-t%s\" \"-2%s\" \"-d%s\" -b%s -u128", temp, temp, target, memory);
            cmd = String.format("nohup sh -c '%s' > %s &", cmd, logLocation);
            System.out.println("Starting process with the command: " + cmd);
            ProcessBuilder processBuilder = new ProcessBuilder("nohup", "sh", "-c", cmd, " > " + logLocation + " &");
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
