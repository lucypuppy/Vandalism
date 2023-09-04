package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;

public class MessageEncryptorModule extends Module {

    private final static char OFFSET_CHAR = '\u3400', CHECK_CHAR = '\u3800';

    public MessageEncryptorModule() {
        super(
                "Message Encryptor",
                "This module encrypts your chat messages.",
                FeatureCategory.MISC,
                false,
                false
        );
    }

    public String encryptMessage(final String message) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final char c : message.toCharArray()) {
            stringBuilder.append((char) ((c + OFFSET_CHAR) ^ 98));
        }
        return stringBuilder.toString();
    }

    public String decodeMessage(final String message) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final char c : message.toCharArray()) {
            if (c >= OFFSET_CHAR && c < CHECK_CHAR) {
                stringBuilder.append((char) ((c - OFFSET_CHAR) ^ 98));
                continue;
            }

            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public boolean isEncrypted(final String text) {
        for (final char c : text.toCharArray()) {
            if (c >= OFFSET_CHAR && c < CHECK_CHAR) {
                return true;
            }
        }
        return false;
    }

}
