package me.nekosarekawaii.foxglove.util.minecraft;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;

//TODO: Recode this
public class ChatUtils {

    private static final MutableText chatPrefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("(")
            .append(
                    Text
                            .literal(Foxglove.getInstance().getName())
                            .setStyle(
                                    Style.EMPTY.withColor(TextColor.fromRgb(Foxglove.getInstance().getColorRGB()))
                            )
            )
            .append(") ");

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

    private static final MutableText infoPrefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(
                    Text
                            .literal("Info")
                            .setStyle(
                                    Style.EMPTY.withColor(TextColor.fromRgb(Color.GREEN.getRGB()))
                            )
            )
            .append("] ");

    public static void infoChatMessage(final String message) {
        infoChatMessage(Text.literal(message));
    }

    public static void infoChatMessage(final Text message) {
        chatMessage(infoPrefix.copy().append(message));
    }

    private static final MutableText warningPrefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(
                    Text
                            .literal("Warning")
                            .setStyle(
                                    Style.EMPTY.withColor(TextColor.fromRgb(Color.ORANGE.getRGB()))
                            )
            )
            .append("] ");

    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    public static void warningChatMessage(final Text message) {
        chatMessage(warningPrefix.copy().append(message));
    }

    private static final MutableText errorPrefix = Text.literal("")
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(
                    Text
                            .literal("Error")
                            .setStyle(
                                    Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()))
                            )
            )
            .append("] ");

    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    public static void errorChatMessage(final Text message) {
        chatMessage(errorPrefix.copy().append(message));
    }

    public static MutableText getAsMutableText(final OrderedText text) {
        final LickMyFuckingBallsMojangStringVisitor visitor = new LickMyFuckingBallsMojangStringVisitor();
        text.accept(visitor);
        return visitor.getMutableText();
    }

}
