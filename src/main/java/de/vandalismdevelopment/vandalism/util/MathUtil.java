package de.vandalismdevelopment.vandalism.util;

public class MathUtil {

    public static long cpsToMs(final int cps) {
        return 1000L / cps;
    }

    private static final String[] UNITS = new String[]{"Bytes", "KiB", "MiB", "GiB", "TiB", "PiB"};

    public static String addFormatToByteCount(final long byteCount) {
        if (byteCount == 0) return "0.00 Bytes";
        final int digitGroups = (int) (Math.log(byteCount) / Math.log(1024));
        return String.format("%.2f %s", byteCount / Math.pow(1024, digitGroups), UNITS[digitGroups]);
    }

}
