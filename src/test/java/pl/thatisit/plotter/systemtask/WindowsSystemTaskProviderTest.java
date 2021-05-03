package pl.thatisit.plotter.systemtask;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static pl.thatisit.plotter.domain.K.K_32;

public class WindowsSystemTaskProviderTest {
    private static String data =
            "\n" +
                    "Node,CommandLine,ProcessId\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\20210501-203341-46ff-8937-420107187303 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3990 -u128 -r2 -a3337690719,20210501183942.817572+120,9300\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" ,20210501183942.105482+120,20804\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" --type=gpu-process --field-trial-handle=1552,9011923592517094884,8285865786163236157,131072 --enable-features=WebComponentsV0Enabled --disable-features=CookiesWithoutSameSiteMustBeSecure,SameSiteByDefaultCookies,SpareRendererForSitePerProcess --gpu-preferences=MAAAAAAAAADgAAAwAAAAAAAAAAAAAAAAAABgAAAAAAAQAAAAAAAAAAAAAAAAAAAAKAAAAAQAAAAgAAAAAAAAACgAAAAAAAAAMAAAAAAAAAA4AAAAAAAAABAAAAAAAAAAAAAAAAUAAAAQAAAAAAAAAAAAAAAGAAAAEAAAAAAAAAABAAAABQAAABAAAAAAAAAAAQAAAAYAAAA= --mojo-platform-channel-handle=1564 /prefetch:2,20210501183942.817572+120,20760\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" --type=utility --utility-sub-type=network.mojom.NetworkService --field-trial-handle=1552,9011923592517094884,8285865786163236157,131072 --enable-features=WebComponentsV0Enabled --disable-features=CookiesWithoutSameSiteMustBeSecure,SameSiteByDefaultCookies,SpareRendererForSitePerProcess --lang=pl --service-sandbox-type=network --mojo-platform-channel-handle=1972 /prefetch:8,20210501183942.844704+120,19596\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" --type=renderer --field-trial-handle=1552,9011923592517094884,8285865786163236157,131072 --enable-features=WebComponentsV0Enabled --disable-features=CookiesWithoutSameSiteMustBeSecure,SameSiteByDefaultCookies,SpareRendererForSitePerProcess --lang=pl --app-user-model-id=com.squirrel.chia-blockchain.Chia --app-path=\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar\" --node-integration --no-sandbox --no-zygote --preload=\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar\\build\\electron/preload.js\" --enable-remote-module --background-color=#ffffff --enable-spellcheck --enable-websql --disable-electron-site-instance-overrides --device-scale-factor=1.75 --num-raster-threads=4 --enable-main-frame-before-activation --renderer-client-id=4 --no-v8-untrusted-code-mitigations --mojo-platform-channel-handle=2248 /prefetch:1,20210501183942.861450+120,5780\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\plot1 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,20210501215630.004439+120,5824\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tH:\\plot2 -2H:\\plot2 -dJ:\\ -b3390 -u128 -r2 -a3337690719,20210501215656.581031+120,21576\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tE:\\temp\\plot4 -2E:\\temp\\plot4 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,20210501215727.652100+120,18564\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tF:\\temp -2F:\\temp \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,20210501215749.703943+120,12356\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\plot1 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,20210501223533.216144+120,8728\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tH:\\plot2 -2H:\\plot2 -dJ:\\ -b3390 -u128 -r2 -a3337690719,20210501224158.322888+120,22804\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tE:\\temp\\plot4 -2E:\\temp\\plot4 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,20210501224731.418890+120,1336\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\plot1 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,20210501231434.383085+120,2448\n";

    @Mock
    private WindowsProcessCsvProvider windowsProcessCsvProvider;

    private WindowsSystemTaskProvider windowsSystemTaskProvider;

    @BeforeMethod
    public void init() {
        initMocks(this);
        windowsSystemTaskProvider = new WindowsSystemTaskProvider(windowsProcessCsvProvider);
    }

    @Test
    public void shouldListProcesses() {
        given(windowsProcessCsvProvider.getProcessesCsv()).willReturn(
                new InputStreamReader(new ByteArrayInputStream(data.getBytes())));

        final var result = windowsSystemTaskProvider.plotterProcesses();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId()).isEqualTo("20210501-203341-46ff-8937-420107187303");
        assertThat(result.get(0).getTempDrive()).isEqualTo("G:");
        assertThat(result.get(0).getTargetDrive()).isEqualTo("J:");
        assertThat(result.get(0).getStarted().toString()).isEqualTo("2021-05-01T18:39:42");
        assertThat(result.get(0).getK()).isEqualTo(K_32);
    }
}