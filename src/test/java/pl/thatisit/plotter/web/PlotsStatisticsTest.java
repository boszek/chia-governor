package pl.thatisit.plotter.web;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class PlotsStatisticsTest {

    PlotsStatistics victim;

    @Mock
    PlotsScrapper plotsScrapper;

    @BeforeMethod
    public void init() {
        initMocks(this);
        victim = new PlotsStatistics(plotsScrapper);
    }

    @Test
    public void shouldParseDate() {
        given(plotsScrapper.findPlotFiles()).willReturn(List.of("plot-k32-2021-04-28-07-07-4d2cc78654f4b9047883828c667d709a385d05e77d7f662aeae6101cca703476.plot"));

        var result = victim.get();

        assertThat(result.get(0).getDate()).isEqualTo("2021-04-28");
        assertThat(result.get(0).getCount()).isEqualTo(1);
    }

}