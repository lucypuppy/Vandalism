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
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Optional;

public class ChatUtil implements MinecraftWrapper {

    private enum Type {

        INFO(Color.GREEN), WARNING(Color.ORANGE), ERROR(Color.RED);

        private final MutableText prefix;

        Type(final Color color) {
            this.prefix = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).append("[").append(Text.literal(StringUtils.normalizeEnumName(this.name())).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB())))).append("] ");
        }

        public MutableText getPrefix() {
            return this.prefix.copy();
        }

    }

    private static final MutableText CHAT_PREFIX = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).append("(").append(Text.literal(FabricBootstrap.MOD_NAME).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.WHITE.getRGB())))).append(") ");

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
