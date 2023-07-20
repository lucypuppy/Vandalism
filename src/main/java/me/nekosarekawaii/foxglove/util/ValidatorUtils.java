package me.nekosarekawaii.foxglove.util;

import java.util.regex.Pattern;

public class ValidatorUtils {

    private final static Pattern uuidPattern = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isUUID(final String input) {
        return uuidPattern.matcher(input).matches();
    }

    public static boolean isInteger(final String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException ignored) {
            return false;
        }
    }

}
