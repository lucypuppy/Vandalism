package me.nekosarekawaii.foxglove.util;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;

/**
 * The ChatUtils class provides utility methods for displaying chat messages with different prefixes and styles in Minecraft.
 */
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

    /**
     * Adds an empty chat message (a line break) to the chat HUD.
     * This method does nothing if the player is not in a world.
     */
    public static void emptyChatMessage() {
        if (MinecraftClient.getInstance().world == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("\n"));
    }

    /**
     * Sends a chat message with the specified string to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The message to send as a string.
     */
    public static void chatMessage(final String message) {
        chatMessage(Text.literal(message));
    }

    /**
     * Sends a chat message with the specified text to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The message to send as Text.
     */
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

    /**
     * Sends an info chat message with the specified string to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The info message to send as a string.
     */
    public static void infoChatMessage(final String message) {
        infoChatMessage(Text.literal(message));
    }

    /**
     * Sends an info chat message with the specified text to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The info message to send as Text.
     */
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

    /**
     * Sends a warning chat message with the specified string to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The warning message to send as a string.
     */
    public static void warningChatMessage(final String message) {
        warningChatMessage(Text.literal(message));
    }

    /**
     * Sends a warning chat message with the specified text to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The warning message to send as Text.
     */
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

    /**
     * Sends an error chat message with the specified string to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The error message to send as a string.
     */
    public static void errorChatMessage(final String message) {
        errorChatMessage(Text.literal(message));
    }

    /**
     * Sends an error chat message with the specified text to the chat HUD.
     * This method does nothing if the player is not in a world.
     *
     * @param message The error message to send as Text.
     */
    public static void errorChatMessage(final Text message) {
        chatMessage(errorPrefix.copy().append(message));
    }

}
