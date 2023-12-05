package de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.ChatListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

//TODO: Add dhjihad mode.

public class MessageEncryptorModule extends Module implements ChatListener {

    public final static MutableText ENCRYPTION_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("E").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff2626))))
            .append("]");

    private final static char OFFSET_CHAR = '\u3400', CHECK_CHAR = '\u3800', CHECK_CHAR_2 = '\u0025';

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
            final char encryptedChar = (char) ((c ^ 98) + OFFSET_CHAR);

            if (c == CHECK_CHAR_2) {
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
            final char decryptedChar = (char) ((c - OFFSET_CHAR) ^ 98);
            if (decryptedChar == CHECK_CHAR_2) {
                isEncrpyted = !isEncrpyted;
                isOldCorona = false;
                continue;
            }
            if (c >= OFFSET_CHAR && c < CHECK_CHAR && (isEncrpyted || isOldCorona)) {
                stringBuilder.append(decryptedChar);
                continue;
            }

            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public boolean isEncrypted(final String text) {
        for (final char c : text.toCharArray()) {
            if (c >= OFFSET_CHAR && c < CHECK_CHAR)
                return true;
        }
        return false;
    }

}
