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
import de.florianmichael.rclasses.common.color.ColorUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
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
import java.util.UUID;

public class ChatUtil implements MinecraftWrapper {

    private static final MutableText BRACKET_COLOR = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));

    public enum Type {

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

    public static void infoChatMessage(final Text message, final boolean sameLine) {
        chatMessage(Type.INFO.getPrefix().copy().append(message), true, sameLine);
    }

    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    public static void warningChatMessage(final Text message) {
        if (mc.inGameHud == null) {
            Vandalism.getInstance().getLogger().warn(message.getString());
            return;
        }
        chatMessage(Type.WARNING.getPrefix().copy().append(message));
    }

    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    public static void errorChatMessage(final Text message) {
        if (mc.inGameHud == null) {
            Vandalism.getInstance().getLogger().error(message.getString());
            return;
        }
        chatMessage(Type.ERROR.getPrefix().copy().append(message));
    }

    public static void emptyChatMessage() {
        chatMessage(Text.literal("\n"));
    }

    public static void chatMessage(final String message) {
        chatMessage(Text.literal(message));
    }

    public static void chatMessage(final MutableText message) {
        chatMessage(message, true);
    }

    public static void chatMessage(final String message, final boolean prefix) {
        chatMessage(Text.literal(message), prefix);
    }

    public static void chatMessage(final MutableText message, final boolean prefix) {
        chatMessage(message, prefix, false);
    }

    public static final Text SAME_LINE_ID = Text.literal(UUID.randomUUID().toString());

    public static void chatMessage(final MutableText message, final boolean prefix, final boolean sameLine) {
        if (mc.inGameHud == null) {
            Vandalism.getInstance().getLogger().info(message.getString());
            return;
        }
        final MutableText text = prefix ? getChatPrefix().copy().append(message) : message;
        if (sameLine && Vandalism.getInstance().getClientSettings().getChatSettings().sameLineMessages.getValue()) {
            text.getSiblings().add(SAME_LINE_ID);
        }
        mc.inGameHud.getChatHud().addMessage(text);
    }

    public static void sendChatMessage(final String message) {
        final ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler == null) return;
        final Instant instant = Instant.now();
        final long secureRandom = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        final LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = networkHandler.lastSeenMessagesCollector.collect();
        final MessageSignatureData messageSignatureData = networkHandler.messagePacker.pack(new MessageBody(message, instant, secureRandom, lastSeenMessages.lastSeen()));
        networkHandler.sendPacket(new ChatMessageC2SPacket(message, instant, secureRandom, messageSignatureData, lastSeenMessages.update()));
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

    public static MutableText colorFade(final String text, final Style style, final Color startColor, final Color endColor) {
        final MutableText mutableText = Text.empty();

        for (int i = 0; i < text.length(); i++) {
            final float percent = (float) i / (text.length() - 1);
            final Color color = ColorUtils.colorInterpolate(startColor, endColor, percent);

            mutableText.append(Text.literal(String.valueOf(text.charAt(i)))
                    .setStyle(style.withColor(TextColor.fromRgb(color.getRGB()))));
        }

        return mutableText;
    }

    public static MutableText getChatPrefix() {
        final ColorValue chatPrefixColor = Vandalism.getInstance().getClientSettings().getChatSettings().chatPrefixColor;
        final MutableText prefix;

        if (chatPrefixColor.getMode().getValue() == ColorValue.ColorMode.STATIC) {
            prefix = Text.literal(FabricBootstrap.MOD_NAME)
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(chatPrefixColor.getColor(0).getRGB())));
        } else {
            prefix = colorFade(FabricBootstrap.MOD_NAME, Style.EMPTY,
                    chatPrefixColor.getColor(0), chatPrefixColor.getColor(1000));
        }

        return BRACKET_COLOR.copy()
                .append("\u300C")
                .append(prefix)
                .append("\u300D");
    }

}
