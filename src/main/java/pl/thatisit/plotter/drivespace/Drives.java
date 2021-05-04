package pl.thatisit.plotter.drivespace;

import java.io.File;

public interface Drives {
    String getDrive(String path);

    File findFile(String path, String name);
}
