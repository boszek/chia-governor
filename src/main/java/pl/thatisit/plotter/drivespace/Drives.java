package pl.thatisit.plotter.drivespace;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;

public final class Drives {
    public static String getDrive(String path) {
        return Path.of(path).getRoot().toString().replaceAll("[\\\\/]", "").toUpperCase();
    }

    public static File findFile(String path, String name) {
        var files = (Collection<File>)FileUtils.listFiles(new File(path), new String[]{"exe"}, true);
        return files.stream()
                .filter(file -> file.getName().equals(name))
                .max(Comparator.comparing(File::getAbsolutePath))
                .orElseThrow(() -> new IllegalStateException("No " + name + " file was found at " + path));
    }

}
