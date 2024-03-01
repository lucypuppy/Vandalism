/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.util.game;

import de.florianmichael.rclasses.common.StringUtils;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;

public class ChatUtil implements MinecraftWrapper {

    public static final MutableText CHAT_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("(")
            .append(Text.literal(FabricBootstrap.MOD_NAME)
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.WHITE.getRGB()))))
            .append(") ");

    private enum Type {

        INFO(Color.GREEN), WARNING(Color.ORANGE), ERROR(Color.RED);

        private final MutableText prefix;

        Type(final Color color) {
            this.prefix = Text.empty()
                    .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
                    .append("[")
                    .append(Text.literal(StringUtils.normalizeEnumName(this.name()))
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB()))))
                    .append("] ");
        }

        public MutableText getPrefix() {
            return this.prefix.copy();
        }

    }

    public static void infoChatMessage(final String message) {
        infoChatMessage(Text.literal(message));
    }

    public static void infoChatMessage(final Text message) {
        chatMessage(Type.INFO.getPrefix().copy().append(message));
    }

    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    public static void warningChatMessage(final Text message) {
        chatMessage(Type.WARNING.getPrefix().copy().append(message));
    }

    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    public static void errorChatMessage(final Text message) {
        chatMessage(Type.ERROR.getPrefix().copy().append(message));
    }

    public static void emptyChatMessage() {
        chatMessage(Text.literal("\n"));
    }

    public static void chatMessage(final String message) {
        chatMessage(Text.literal(message));
    }

    public static void chatMessage(final Text message) {
        chatMessage(message, true);
    }

    public static void chatMessage(final String message, final boolean prefix) {
        chatMessage(Text.literal(message), prefix);
    }

    public static void chatMessage(final Text message, final boolean prefix) {
        mc.inGameHud.getChatHud().addMessage(prefix ? CHAT_PREFIX.copy().append(message) : message);
    }

    public static void sendChatMessage(final String message) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        final Instant instant = Instant.now();
        final long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        final LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = networkHandler.lastSeenMessagesCollector.collect();
        final MessageSignatureData messageSignatureData = networkHandler.messagePacker.pack(new MessageBody(message, instant, l, lastSeenMessages.lastSeen()));
        networkHandler.sendPacket(new ChatMessageC2SPacket(message, instant, l, messageSignatureData, lastSeenMessages.update()));
    }

    public static Text trimText(final Text text, final int length) {
        if (text.getString().length() <= length) {
            return text;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        final Style originalStyle = text.getStyle();
        text.visit((string) -> {
            final int remainingLength = length - stringBuilder.length();
            if (remainingLength <= 0) return StringVisitable.TERMINATE_VISIT;
            else {
                stringBuilder.append(string.length() <= remainingLength ? string : string.substring(0, remainingLength));
                return Optional.empty();
            }
        });
        return Text.literal(stringBuilder.toString()).setStyle(originalStyle);
    }

}
