package de.foxglovedevelopment.foxglove.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.foxglovedevelopment.foxglove.event.ChatListener;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.impl.module.Module;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class MessageEncryptorModule extends Module implements ChatListener {

    public final static MutableText ENCRYPTION_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("E").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff2626))))
            .append("]");

    private static final char offsetChar = '\u3400', checkChar = '\u3800', checkChar2 = '\u0025';

    public MessageEncryptorModule() {
        super(
                "Message Encryptor",
                "This module encrypts every message you sent that starts and ends with '%'" +
                        " (e.g. Hello %1234% -> Hello {encrypted message} [E])",
                FeatureCategory.MISC,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(ChatSendEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(ChatSendEvent.ID, this);
    }

    @Override
    public void onChatSend(final ChatSendEvent event) {
        event.message = this.encryptMessage(event.message);
    }

    private String encryptMessage(final String message) {
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

    public String decryptMessage(final String message) {
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

    public boolean isEncrypted(final String text) {
        for (final char c : text.toCharArray()) {
            if (c >= offsetChar && c < checkChar)
                return true;
        }

        return false;
    }

}
