package pl.thatisit.plotter.logprocessor.systemtask;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class WindowsSystemTaskProviderTest {
    private static String data =
            "\n" +
                    "Node,CommandLine,ProcessId\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\20210501-203341-46ff-8937-420107187303 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3990 -u128 -r2 -a3337690719,9300\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tH:\\plot2 -2H:\\plot2 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,2148\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\plot1 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3990 -u128 -r2 -a3337690719,9028\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tH:\\plot2 -2H:\\plot2 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,3392\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tG:\\plot1 -2G:\\plot1 \"-dJ:\\Chia Plots\" -b3990 -u128 -r2 -a3337690719,14076\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tE:\\temp\\plot4 -2E:\\temp\\plot4 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,9252\n" +
                    "BOSZEK-PC,C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar.unpacked\\daemon/chia.exe plots create -k32 -n1 -tH:\\plot2 -2H:\\plot2 \"-dJ:\\Chia Plots\" -b3390 -u128 -r2 -a3337690719,12684\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" ,20804\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" --type=gpu-process --field-trial-handle=1552,9011923592517094884,8285865786163236157,131072 --enable-features=WebComponentsV0Enabled --disable-features=CookiesWithoutSameSiteMustBeSecure,SameSiteByDefaultCookies,SpareRendererForSitePerProcess --gpu-preferences=MAAAAAAAAADgAAAwAAAAAAAAAAAAAAAAAABgAAAAAAAQAAAAAAAAAAAAAAAAAAAAKAAAAAQAAAAgAAAAAAAAACgAAAAAAAAAMAAAAAAAAAA4AAAAAAAAABAAAAAAAAAAAAAAAAUAAAAQAAAAAAAAAAAAAAAGAAAAEAAAAAAAAAABAAAABQAAABAAAAAAAAAAAQAAAAYAAAA= --mojo-platform-channel-handle=1564 /prefetch:2,20760\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" --type=utility --utility-sub-type=network.mojom.NetworkService --field-trial-handle=1552,9011923592517094884,8285865786163236157,131072 --enable-features=WebComponentsV0Enabled --disable-features=CookiesWithoutSameSiteMustBeSecure,SameSiteByDefaultCookies,SpareRendererForSitePerProcess --lang=pl --service-sandbox-type=network --mojo-platform-channel-handle=1972 /prefetch:8,19596\n" +
                    "BOSZEK-PC,\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\Chia.exe\" --type=renderer --field-trial-handle=1552,9011923592517094884,8285865786163236157,131072 --enable-features=WebComponentsV0Enabled --disable-features=CookiesWithoutSameSiteMustBeSecure,SameSiteByDefaultCookies,SpareRendererForSitePerProcess --lang=pl --app-user-model-id=com.squirrel.chia-blockchain.Chia --app-path=\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar\" --node-integration --no-sandbox --no-zygote --preload=\"C:\\Users\\boszek\\AppData\\Local\\chia-blockchain\\app-1.1.2\\resources\\app.asar\\build\\electron/preload.js\" --enable-remote-module --background-color=#ffffff --enable-spellcheck --enable-websql --disable-electron-site-instance-overrides --device-scale-factor=1.75 --num-raster-threads=4 --enable-main-frame-before-activation --renderer-client-id=4 --no-v8-untrusted-code-mitigations --mojo-platform-channel-handle=2248 /prefetch:1,5780\n";

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
    }
}