package de.nekosarekawaii.foxglove.util.minecraft;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ChatUtils {

    private final static MutableText chatPrefix = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("(")
            .append(Foxglove.getInstance().getClientNameText())
            .append(") ");

    private final static MutableText infoPrefix = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(Text.literal("Info").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.GREEN.getRGB()))))
            .append("] ");

    private final static MutableText warningPrefix = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(Text.literal("Warning").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.ORANGE.getRGB()))))
            .append("] ");

    private final static MutableText errorPrefix = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(Text.literal("Error").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()))))
            .append("] ");

    public static void emptyChatMessage() {
        if (MinecraftClient.getInstance().world == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("\n"));
    }

    public static void chatMessage(final String message) {
        chatMessage(Text.literal(message));
    }

    public static void chatMessage(final Text message) {
        if (MinecraftClient.getInstance().world == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(chatPrefix.copy().append(message));
    }

    public static void infoChatMessage(final String message) {
        infoChatMessage(Text.literal(message));
    }

    public static void infoChatMessage(final Text message) {
        chatMessage(infoPrefix.copy().append(message));
    }

    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    public static void warningChatMessage(final Text message) {
        chatMessage(warningPrefix.copy().append(message));
    }

    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    public static void errorChatMessage(final Text message) {
        chatMessage(errorPrefix.copy().append(message));
    }

    public static MutableText getAsMutableText(final OrderedText text) {
        final StringVisitorAccessor visitor = new StringVisitorAccessor();
        text.accept(visitor);
        return visitor.getMutableText();
    }

}
