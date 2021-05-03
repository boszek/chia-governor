package pl.thatisit.plotter.logprocessor;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.thatisit.plotter.domain.PlotterProcess;

import java.io.InputStreamReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.thatisit.plotter.domain.PlotStatus.*;

public class ProcessLogParserTest {

    @Mock
    private LogLoader logLoader;
    private ProcessLogParser victim;
    private PlotterProcess sample = PlotterProcess.builder().build();

    @BeforeMethod
    public void init() {
        initMocks(this);
        victim = new ProcessLogParser(logLoader);
    }

    @Test
    public void shouldIdentifyStage1() {
        givenLog("plotter_log_stage1.txt");
        assertThat(victim.evaluateStatus(sample).getStatus()).isEqualTo(STAGE1);
    }
    @Test
    public void shouldIdentifyStage2() {
        givenLog("plotter_log_stage2.txt");
        assertThat(victim.evaluateStatus(sample).getStatus()).isEqualTo(STAGE2);
    }
    @Test
    public void shouldIdentifyStage3() {
        givenLog("plotter_log_stage3.txt");
        assertThat(victim.evaluateStatus(sample).getStatus()).isEqualTo(STAGE3);
    }
    @Test
    public void shouldIdentifyStage4() {
        givenLog("plotter_log_stage4.txt");
        assertThat(victim.evaluateStatus(sample).getStatus()).isEqualTo(STAGE4);
    }
    @Test
    public void shouldIdentifyFinished() {
        givenLog("plotter_log_complete.txt");
        assertThat(victim.evaluateStatus(sample).getStatus()).isEqualTo(FINISHED);
    }

    private void givenLog(String resource) {
        given(logLoader.getLogStream(any()))
                .willReturn(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(resource)));
    }
}
