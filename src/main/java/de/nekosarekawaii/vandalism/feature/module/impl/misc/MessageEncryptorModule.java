package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.player.ChatModifyReceiveListener;
import de.nekosarekawaii.vandalism.base.event.player.ChatSendListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class MessageEncryptorModule extends AbstractModule implements ChatSendListener, ChatModifyReceiveListener {

    public static final MutableText ENCRYPTION_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("E").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff2626))))
            .append("]");

    private static final char OFFSET_CHAR = '㐀', CHECK_CHAR = '㠀', CHECK_CHAR_2 = '%';

    public MessageEncryptorModule() {
        super("Message Encryptor", "This module encrypts every message you sent that starts and ends with '%'" +
                        " (e.g. Hello %1234% -> Hello {encrypted message} [E])", Category.MISC);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(ChatSendEvent.ID, this);
        DietrichEvents2.global().subscribe(ChatModifyReceiveEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(ChatSendEvent.ID, this);
        DietrichEvents2.global().unsubscribe(ChatModifyReceiveEvent.ID, this);
    }

    @Override
    public void onChatSend(final ChatSendEvent event) {
        event.message = this.encryptMessage(event.message);
    }

    @Override
    public void onChatModifyReceive(final ChatModifyReceiveEvent event) {
        final String message = event.mutableText.getString();
        if (this.isEncrypted(message)) {
            event.mutableText.append(MessageEncryptorModule.ENCRYPTION_PREFIX.setStyle(
                    MessageEncryptorModule.ENCRYPTION_PREFIX.getStyle().withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.literal(this.decryptMessage(message))
                    )))
            );
        }
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

    private String decryptMessage(final String message) {
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

    private boolean isEncrypted(final String text) {
        for (final char c : text.toCharArray()) {
            if (c >= OFFSET_CHAR && c < CHECK_CHAR)
                return true;
        }
        return false;
    }

}
