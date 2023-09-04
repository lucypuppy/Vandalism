package de.nekosarekawaii.foxglove.util;

public class MessageEncryptUtil {

    private static final char offsetChar = '\u3400', checkChar = '\u3800', checkChar2 = '\u0025';

    public static String encryptMessage(final String message) {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean isEncrypting = false;

        for (final char c : message.toCharArray()) {
            final char encryptedChar = (char) ((c ^ 98) + offsetChar);

            if (c == checkChar2) {
                isEncrypting = !isEncrypting;
                stringBuilder.append(encryptedChar);
                continue;
            }

            stringBuilder.append(isEncrypting ? encryptedChar : c);
        }

        return stringBuilder.toString();
    }

    public static String decodeMessage(final String message) {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean isEncrpyted = false, isOldCorona = true;

        for (final char c : message.toCharArray()) {
            final char decryptedChar = (char) ((c - offsetChar) ^ 98);

            if (decryptedChar == checkChar2) {
                isEncrpyted = !isEncrpyted;
                isOldCorona = false;
                continue;
            }

            if (c >= offsetChar && c < checkChar && (isEncrpyted || isOldCorona)) {
                stringBuilder.append(decryptedChar);
                continue;
            }

            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    public static boolean isEncrypted(final String text) {
        for (final char c : text.toCharArray()) {
            if (c >= offsetChar && c < checkChar)
                return true;
        }

        return false;
    }

}
