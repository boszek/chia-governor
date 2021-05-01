package pl.thatisit.plotter.drivespace;

import java.io.File;

public final class DriveSpace {

    public static long getFreeSpace(String path) {
        String drive = path.split("/")[0];
        return new File(drive).getFreeSpace();
    }

    public static boolean canAllocate(String path, long size) {
        return getFreeSpace(path) > size;
    }
}
