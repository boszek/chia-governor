package pl.thatisit.plotter.systemtask.linux;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.thatisit.plotter.drivespace.Drives;
import pl.thatisit.plotter.systemtask.linux.LinuxProcessCsvProvider;
import pl.thatisit.plotter.systemtask.linux.LinuxSystemTaskProvider;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

public class LinuxSystemTaskProviderTest {

    private static String output = "  10640,/home/boszek/projects/chia-blockchain/venv/bin/python3.8 /home/boszek/projects/chia-blockchain/venv/bin/chia plots create -k32 -n1 -t/media/boszek/Chia Toshiba 4TB/temp -2/media/boszek/Chia Toshiba 4TB/temp -d/media/boszek/Chia Toshiba 4TB/Chia Plots -b3390 -u128 -r2 -a3337690719\n" +
            "  10641,/home/boszek/projects/chia-blockchain/venv/bin/python3.8 /home/boszek/projects/chia-blockchain/venv/bin/chia plots create -k32 -n1 -t/media/boszek/Chia Toshiba 4TB/temp -2/media/boszek/Chia Toshiba 4TB/temp -d/media/boszek/Chia Toshiba 4TB/Chia Plots -b3390 -u128 -r2 -a3337690719\n" +
            "  11321,grep --color=auto chia plots create";

    private LinuxSystemTaskProvider victim;

    @BeforeMethod
    public void init() {
        var csvProvider = mock(LinuxProcessCsvProvider.class);
        var drives = mock(Drives.class);
        given(csvProvider.getProcessesCsv()).willReturn(List.of(output.split("\n")));
        given(drives.getDrive(eq("/media/boszek/Chia Toshiba 4TB/temp"))).willReturn("/dev/sda");
        given(drives.getDrive(eq("/media/boszek/Chia Toshiba 4TB/Chia Plots"))).willReturn("/dev/sda");
        victim = new LinuxSystemTaskProvider(csvProvider, drives);

    }

    @Test
    public void shouldParseProcesses() {
        var result = victim.plotterProcesses();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTempDrive()).isEqualTo("/dev/sda");
        assertThat(result.get(0).getTargetDrive()).isEqualTo("/dev/sda");
    }

}