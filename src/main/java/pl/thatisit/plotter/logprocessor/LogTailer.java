package pl.thatisit.plotter.logprocessor;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import java.io.File;
import java.util.function.Consumer;

public class LogTailer {
    private final Tailer tailer;
    private final Consumer<String> logConsumer;

    public LogTailer(File file, Consumer<String> logConsumer) {
        tailer = Tailer.create(file, new LogTailerListener(), 1000);
        this.logConsumer = logConsumer;
    }

    public void close() {
        tailer.stop();
    }

    private class LogTailerListener implements TailerListener {

        @Override
        public void init(Tailer tailer) {

        }

        @Override
        public void fileNotFound() {

        }

        @Override
        public void fileRotated() {

        }

        @Override
        public void handle(String s) {
            logConsumer.accept(s);
        }

        @Override
        public void handle(Exception e) {

        }
    }
}
