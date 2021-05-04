package pl.thatisit.plotter.domain;

public enum K {

    K_32(32);

    private final int value;
    private static final long GB = 1024L * 1024L * 1024L;

    K(int value) {
        this.value = value;
    }

    public static K of(String value) {
        switch (value) {
            case "32":
                return K_32;
        }
        throw new IllegalArgumentException("no K configured for k=" + value);
    }

    public long getRequiredTempSpace() {
        if (value == 32) {
            return 239 * GB;
        }
        throw new IllegalArgumentException("no required space configured for k=" + value);
    }

    public long getRequiredTargetSpace() {
        if (value == 32) {
            return (long) (101.4d * GB);
        }
        throw new IllegalArgumentException("no required space configured for k=" + value);
    }
}
