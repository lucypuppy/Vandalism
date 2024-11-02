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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.injection.access.IClientPlayNetworkHandler;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import lombok.Getter;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Optional;
import java.util.UUID;

public class ChatUtil implements MinecraftWrapper {

    private static final MutableText BRACKET_COLOR = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));

    public enum Type implements IName {

        INFO(Color.GREEN), WARNING(Color.ORANGE), ERROR(Color.RED);

        @Getter
        private final int color;
        private final String name;

        Type(final Color color) {
            this.color = color.getRGB();
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    public static void infoChatMessage(final String message) {
        infoChatMessage(Text.literal(message));
    }

    public static void infoChatMessage(final Text message) {
        infoChatMessage(message, false);
    }

    public static void infoChatMessage(final Text message, final boolean sameLine) {
        chatMessage(message.copy().withColor(Type.INFO.getColor()), true, sameLine);
    }

    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    public static void warningChatMessage(final Text message) {
        warningChatMessage(message, false);
    }

    public static void warningChatMessage(final Text message, final boolean sameLine) {
        if (mc.inGameHud == null) {
            Vandalism.getInstance().getLogger().warn(message.getString());
            return;
        }
        chatMessage(message.copy().withColor(Type.WARNING.getColor()), true, sameLine);
    }

    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    public static void errorChatMessage(final Text message) {
        errorChatMessage(message, false);
    }

    public static void errorChatMessage(final Text message, final boolean sameLine) {
        if (mc.inGameHud == null) {
            Vandalism.getInstance().getLogger().error(message.getString());
            return;
        }
        chatMessage(message.copy().withColor(Type.ERROR.getColor()), true, sameLine);
    }

    public static void emptyChatMessage() {
        emptyChatMessage(true);
    }

    public static void emptyChatMessage(final boolean prefix) {
        chatMessage(Text.literal(" "), prefix);
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

    public static void chatMessageToServer(final String message) {
        if (message.startsWith("/") && message.length() > 1) {
            mc.getNetworkHandler().sendChatCommand(message.substring(1));
            return;
        }

        ((IClientPlayNetworkHandler) mc.getNetworkHandler()).vandalism$sendChatMessage(message);
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

            mutableText
                    .append(Text.literal(String.valueOf(text.charAt(i)))
                            .setStyle(style.withColor(TextColor.fromRgb(color.getRGB()))));
        }

        return mutableText;
    }

    public static MutableText interpolateTextColor(final String text, final Color color1, final Color color2) {
        if (color1 == null || color2 == null) {
            throw new IllegalArgumentException("Colors can't be null.");
        }
        final MutableText newText = Text.empty();
        final int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            final Color color = ColorUtils.colorInterpolate(color1, color2, i / (textLength - 1.0));
            newText.append(Text.literal(String.valueOf(text.charAt(i))).setStyle(Style.EMPTY.withColor(color.getRGB())));
        }
        return newText;
    }

    public static MutableText getChatPrefix() {
        final ColorValue chatPrefixColor = Vandalism.getInstance().getClientSettings().getChatSettings().chatPrefixColor;
        final MutableText prefix;

        if (chatPrefixColor.getMode().getValue() == ColorValue.ColorMode.STATIC) {
            prefix = Text.literal(FabricBootstrap.MOD_NAME).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(chatPrefixColor.getColor(0).getRGB())));
        } else {
            prefix = colorFade(FabricBootstrap.MOD_NAME, Style.EMPTY, chatPrefixColor.getColor(0), chatPrefixColor.getColor(1000));
        }

        return BRACKET_COLOR.copy()
                .append(Vandalism.getInstance().getClientSettings().getChatSettings().startBracket.getValue())
                .append(prefix)
                .append(Vandalism.getInstance().getClientSettings().getChatSettings().endBracket.getValue())
                .append(" ");
    }

}
