package pl.thatisit.plotter.runner;

import org.testng.annotations.Test;
import pl.thatisit.plotter.DateUUID;
import pl.thatisit.plotter.config.ConfigurationManager;

import static org.testng.Assert.*;

public class PlotProcessRunnerTest {

    @Test
    public void shouldRunProcess() {
        new PlotProcessRunner(ConfigurationManager.get())
                .startProcess("G:", DateUUID.randomDateUUID());
    }

}