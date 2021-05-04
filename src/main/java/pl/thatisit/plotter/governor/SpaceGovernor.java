package pl.thatisit.plotter.governor;

import org.apache.commons.io.FileUtils;
import pl.thatisit.plotter.domain.DiskInfo;
import pl.thatisit.plotter.drivespace.Drives;

import java.io.File;

class SpaceGovernor {
    private final Governor governor;
    private final Drives drives;

    public SpaceGovernor(Governor governor, Drives drives) {
        this.governor = governor;
        this.drives = drives;
    }

    public DiskInfo diskInfo(String temp) {
        var drive = drives.getDrive(temp);
        var driveFile = new File(drive);
        var freeSpace = driveFile.getFreeSpace();
        var tempFile = new File(temp);
        var usedByTemp = tempFile.exists() ? FileUtils.sizeOfDirectory(tempFile) : 0;
        var allocated = governor.processes
                .stream()
                .filter(plotterProcess -> drives.getDrive(plotterProcess.getTempDrive()).equals(drive))
                .mapToLong(plotterProcess -> plotterProcess.getK().getRequiredTempSpace())
                .sum();
        var usableFree = freeSpace + usedByTemp - allocated;

        return DiskInfo.builder()
                .freeSpace(freeSpace)
                .allocated(allocated)
                .usableFreeSpace(usableFree)
                .totalSize(driveFile.getTotalSpace())
                .drive(drive)
                .build();
    }
}
