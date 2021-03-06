package pl.thatisit.plotter.systemtask.windows;

import java.io.IOException;
import java.io.InputStreamReader;

class WindowsProcessCsvProvider {

    private final String TASKLIST_COMMAND = " wmic process where caption=\"chia.exe\" get commandline,processid,creationdate /format:csv";

    InputStreamReader getProcessesCsv() {
        try {
            var process = Runtime.getRuntime().exec(TASKLIST_COMMAND);
            return new InputStreamReader(process.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
