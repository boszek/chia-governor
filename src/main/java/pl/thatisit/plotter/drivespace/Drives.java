package pl.thatisit.plotter.drivespace;

import java.io.File;
import java.nio.file.Path;

public final class Drives {
    public static String getDrive(String path) {
        return Path.of(path).getRoot().toString().replaceAll("[\\\\/]", "").toUpperCase();
    }
}
