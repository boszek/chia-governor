package pl.thatisit.plotter.drivespace;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;

public class WindowsDrives implements Drives {
    @Override
    public String getDrive(String path) {
        return Paths.get(path).getRoot().toString().replaceAll("\\\\", "");
    }

    @Override
    public File findFile(String path, String name) {
        var files = (Collection<File>) FileUtils.listFiles(new File(path), new String[]{"exe"}, true);
        return files.stream()
                .filter(file -> file.getName().equals(name))
                .max(Comparator.comparing(File::getAbsolutePath))
                .orElseThrow(() -> new IllegalStateException("No " + name + " file was found at " + path));
    }
}
