/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.ChatModifyReceiveListener;
import de.nekosarekawaii.vandalism.event.player.ChatSendListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class MessageEncryptorModule extends Module implements ChatSendListener, ChatModifyReceiveListener {

    private static final MutableText ENCRYPTION_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(" [")
            .append(Text.literal("E").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xff2626))))
            .append("]");

    private static final char OFFSET_CHAR = '㐀', CHECK_CHAR = '㠀', CHECK_CHAR_2 = '%';

    public MessageEncryptorModule() {
        super(
                "Message Encryptor",
                "This module encrypts every message you sent that starts and ends with '%'\n" +
                        "(e.g. Hello %1234% -> Hello {encrypted message} [E])",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(ChatSendEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(ChatModifyReceiveEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(ChatSendEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(ChatModifyReceiveEvent.ID, this);
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
