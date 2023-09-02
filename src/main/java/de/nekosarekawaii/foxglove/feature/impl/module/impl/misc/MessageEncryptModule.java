package de.nekosarekawaii.foxglove.feature.impl.module.impl.misc;

import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;

@ModuleInfo(name = "Message Encrypt", description = "This module encrypts your messages like the old corona module.", category = FeatureCategory.MISC)
public class MessageEncryptModule extends Module {

    private final char offsetChar = '\u3400', checkChar = '\u3800';

    public String encryptMessage(final String message) {
        final var stringBuilder = new StringBuilder();

        for (final var c : message.toCharArray()) {
            stringBuilder.append((char) ((c + offsetChar) ^ 98));
        }

        return stringBuilder.toString();
    }

    public String decodeMessage(String text) {
        final var stringBuilder = new StringBuilder();

        for (final var c : text.toCharArray()) {
            if (c >= offsetChar) {
                stringBuilder.append((char) ((c - offsetChar) ^ 98));
            } else {
                stringBuilder.append(c);
            }
        }

        return stringBuilder.toString();
    }

    public boolean isEncrypted(String text) {
        for (final var c : text.toCharArray()) {
            if (c >= offsetChar && c < checkChar)
                return true;
        }
        return false;
    }

}
