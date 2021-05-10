package pl.thatisit.plotter.logprocessor;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.thatisit.plotter.domain.DateUUID;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.InputStreamReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.thatisit.plotter.logprocessor.PlotStatus.*;

public class ProcessLogParserTest {

    @Mock
    private LogLoader logLoader;
    private final PlotterProcess sample = PlotterProcess.builder().id(DateUUID.randomDateUUID().toString()).build();

    @BeforeMethod
    public void init() {
        initMocks(this);
        ProcessLogParser.init(logLoader);
    }

    @Test
    public void shouldIdentifyStage1() {
        givenLog("plotter_log_stage1.txt");
        var result = ProcessLogParser.evaluateStatus(sample);
        assertThat(result.getStatus()).isEqualTo(STAGE1);
        assertThat(result.getProgress().toString()).isEqualTo("Stage1 table: 2 bucket: 26; ");
    }

    @Test
    public void shouldIdentifyStage2() {
        givenLog("plotter_log_stage2.txt");
        var result = ProcessLogParser.evaluateStatus(sample);
        assertThat(result.getStatus()).isEqualTo(STAGE2);
        assertThat(result.getProgress().toString()).isEqualTo("Stage1 finished in: 5h20m35s; Stage2 table: 3; ");
    }

    @Test
    public void shouldIdentifyStage3() {
        givenLog("plotter_log_stage3.txt");
        var result = ProcessLogParser.evaluateStatus(sample);
        assertThat(result.getStatus()).isEqualTo(STAGE3);
        assertThat(result.getProgress().toString()).isEqualTo("Stage1 finished in: 5h20m35s; Stage2 finished in: 1h14m16s; Stage3 table: 2 bucket: 16; ");
    }

    @Test
    public void shouldIdentifyStage4() {
        givenLog("plotter_log_stage4.txt");
        var result = ProcessLogParser.evaluateStatus(sample);
        assertThat(result.getStatus()).isEqualTo(STAGE4);
        assertThat(result.getProgress().toString()).isEqualTo("Stage1 finished in: 5h20m35s; Stage2 finished in: 1h14m16s; Stage3 finished in: 2h19m11s; Stage4 bucket: 71; ");
    }

    @Test
    public void shouldIdentifyFinished() {
        givenLog("plotter_log_complete.txt");
        var result = ProcessLogParser.evaluateStatus(sample);
        assertThat(result.getStatus()).isEqualTo(FINISHED);
        assertThat(result.getProgress().toString()).isEqualTo("Stage1 finished in: 5h20m35s; Stage2 finished in: 1h14m16s; Stage3 finished in: 2h19m11s; Stage4 finished in: 0h9m26s; ");
    }

    private void givenLog(String resource) {
        given(logLoader.getLogStream(any()))
                .willReturn(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(resource)));
    }
}
