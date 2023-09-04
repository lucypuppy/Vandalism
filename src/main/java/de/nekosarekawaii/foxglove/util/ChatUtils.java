package de.nekosarekawaii.foxglove.util;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ChatUtils {

    private enum Type implements EnumNameNormalizer {

        INFO(Color.GREEN),
        WARNING(Color.ORANGE),
        ERROR(Color.RED);

        private final MutableText prefix;

        Type(final Color color) {
            this.prefix = Text.empty()
                    .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
                    .append("[")
                    .append(
                            Text.literal(this.normalizeName(this.name()))
                                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB())))
                    )
                    .append("] ");
        }

        public MutableText getPrefix() {
            return this.prefix.copy();
        }

    }

    private final static MutableText CHAT_PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("(")
            .append(FormattingUtils.interpolateTextColor(Foxglove.getInstance().getName(), Color.MAGENTA, Color.PINK))
            .append(") ");

    public static void emptyChatMessage() {
        if (MinecraftClient.getInstance().world == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("\n"));
    }

    public static void chatMessage(final String message) {
        chatMessage(Text.literal(message));
    }

    public static void chatMessage(final String message, final boolean prefix) {
        chatMessage(Text.literal(message), prefix);
    }

    public static void chatMessage(final Text message) {
        chatMessage(message, true);
    }

    public static void chatMessage(final Text message, final boolean prefix) {
        if (MinecraftClient.getInstance() == null) return;
        if (MinecraftClient.getInstance().world == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(prefix ? CHAT_PREFIX.copy().append(message) : message);
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

    public static MutableText getAsMutableText(final OrderedText text) {
        final StringVisitorAccessor visitor = new StringVisitorAccessor();
        text.accept(visitor);
        return visitor.getMutableText();
    }

}
