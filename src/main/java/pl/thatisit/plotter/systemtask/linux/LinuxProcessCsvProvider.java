package pl.thatisit.plotter.systemtask.linux;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

public class LinuxProcessCsvProvider {
    List<String> getProcessesCsv() {
        try {
            var process = new ProcessBuilder("ps", "-e", "-o %p", "-o ,%a").start();
            return IOUtils.readLines(process.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
