package de.foxglovedevelopment.foxglove.util;

import java.util.regex.Pattern;

public class StringUtils {

    public static String replaceLast(final String input, final String target, final String replacement) {
        if (input == null || target == null || replacement == null) return input;
        return input.replaceFirst("(?s)(.*)" + target, "$1" + replacement);
    }

    public static String replaceIgnoreCase(final String input, final String target, final String replacement) {
        if (input == null || target == null || replacement == null) return input;
        return Pattern.compile(target, Pattern.CASE_INSENSITIVE).matcher(input).replaceAll(replacement);
    }

    public static boolean containsIgnoreCase(final String input, final String target) {
        if (input == null || target == null) return false;
        return input.toLowerCase().contains(target.toLowerCase());
    }

    public static boolean endsWithIgnoreCase(final String input, final String target) {
        if (input == null || target == null) return false;
        return input.toLowerCase().endsWith(target.toLowerCase());
    }

    public static boolean startsWithIgnoreCase(final String input, final String target) {
        if (input == null || target == null) return false;
        return input.toLowerCase().startsWith(target.toLowerCase());
    }

}
