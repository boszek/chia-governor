package pl.thatisit.plotter.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiskInfo {
    String drive;
    long totalSize;
    long freeSpace;
    long usableFreeSpace;
    long allocated;
}
