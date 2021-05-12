package pl.thatisit.plotter.logprocessor;

import pl.thatisit.plotter.domain.PlotterProcess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogProcessorManager {

    private static final Map<String, ProcessLogParser> instances = new ConcurrentHashMap<>();
    private static final Map<String, LogTailer> tailers = new ConcurrentHashMap<>();
    private final LogLoader logLoader;

    public LogProcessorManager(LogLoader logLoader) {
        this.logLoader = logLoader;
    }

    public ProcessLogParser get(PlotterProcess process) {
        if (!instances.containsKey(process.getId())) {
            var parser = new ProcessLogParser(process);
            var tailer = new LogTailer(logLoader.getLogFile(process), parser::processLine);
            instances.put(process.getId(), parser);
            tailers.put(process.getId(), tailer);
        }
        return instances.get(process.getId());
    }

    void close(PlotterProcess process) {
        if (tailers.containsKey(process.getId())) {
            tailers.get(process.getId()).close();
            tailers.remove(process.getId());
            instances.remove(process.getId());
        }
    }
}
