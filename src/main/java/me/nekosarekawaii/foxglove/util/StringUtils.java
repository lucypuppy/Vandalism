package me.nekosarekawaii.foxglove.util;

import java.util.regex.Pattern;

/**
 * The StringUtils class provides utility methods for manipulating and comparing strings.
 */
public class StringUtils {

    /**
     * Replaces the last occurrence of a target string with a replacement string in the input string.
     *
     * @param input       The input string.
     * @param target      The target string to replace.
     * @param replacement The replacement string.
     * @return The resulting string after replacing the last occurrence of the target string.
     */
    public static String replaceLast(final String input, final String target, final String replacement) {
        return input.replaceFirst("(?s)(.*)" + target, "$1" + replacement);
    }

    /**
     * Replaces all occurrences of a target string (case-insensitive) with a replacement string in the input string.
     *
     * @param input       The input string.
     * @param target      The target string to replace.
     * @param replacement The replacement string.
     * @return The resulting string after replacing all occurrences of the target string.
     */
    public static String replaceIgnoreCase(final String input, final String target, final String replacement) {
        return Pattern.compile(target, Pattern.CASE_INSENSITIVE).matcher(input).replaceAll(replacement);
    }

    /**
     * Checks if the input string contains the target string (case-insensitive).
     *
     * @param input  The input string.
     * @param target The target string to search for.
     * @return {@code true} if the input string contains the target string, ignoring case; {@code false} otherwise.
     */
    public static boolean containsIgnoreCase(final String input, final String target) {
        if (input == null || target == null) return false;
        return input.toLowerCase().contains(target.toLowerCase());
    }

    /**
     * Checks if the input string ends with the target string (case-insensitive).
     *
     * @param input  The input string.
     * @param target The target string to compare.
     * @return {@code true} if the input string ends with the target string, ignoring case; {@code false} otherwise.
     */
    public static boolean endsWithIgnoreCase(final String input, final String target) {
        return input.toLowerCase().endsWith(target.toLowerCase());
    }

    /**
     * Checks if the input string starts with the target string (case-insensitive).
     *
     * @param input  The input string.
     * @param target The target string to compare.
     * @return {@code true} if the input string starts with the target string, ignoring case; {@code false} otherwise.
     */
    public static boolean startsWithIgnoreCase(final String input, final String target) {
        return input.toLowerCase().startsWith(target);
    }

}
