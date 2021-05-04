package pl.thatisit.plotter.systemtask;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentsTest {

    @Test
    public void shouldParseWindowsArgument(){
        var arguments = Arguments.of("plots create -k32 -n1 -tG:\\20210501-203341-46ff-8937-420107187303 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3990 -u128 -r2 -a3337690719");
        assertThat(arguments.get("plots")).isEqualTo("plots");
        assertThat(arguments.get("plots")).isEqualTo("plots");
        assertThat(arguments.get("k")).isEqualTo("32");
        assertThat(arguments.get("d")).isEqualTo("J:\\Chia Plots");
        assertThat(arguments.get("a")).isEqualTo("3337690719");
    }


    @Test
    public void shouldParseLinuxArgument(){
        var arguments = Arguments.of("/home/boszek/projects/chia-blockchain/venv/bin/python3.8 /home/boszek/projects/chia-blockchain/venv/bin/chia plots create -k32 -n1 -t/media/boszek/Chia Toshiba 4TB/temp -2/media/boszek/Chia Toshiba 4TB/temp -d/media/boszek/Chia Toshiba 4TB/Chia Plots -b3390 -u128 -r2 -a3337690719");
        assertThat(arguments.get("plots")).isEqualTo("plots");
        assertThat(arguments.get("create")).isEqualTo("create");
        assertThat(arguments.get("k")).isEqualTo("32");
        assertThat(arguments.get("d")).isEqualTo("/media/boszek/Chia Toshiba 4TB/Chia Plots");
        assertThat(arguments.get("a")).isEqualTo("3337690719");
    }

}