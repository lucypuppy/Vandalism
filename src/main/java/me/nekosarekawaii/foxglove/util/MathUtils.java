package me.nekosarekawaii.foxglove.util;

public class MathUtils {

    public static boolean isInteger(final String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (final NumberFormatException ignored) {
            return false;
        }
    }

}
