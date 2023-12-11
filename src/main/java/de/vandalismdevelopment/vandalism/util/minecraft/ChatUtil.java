package de.vandalismdevelopment.vandalism.util.minecraft;

import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;

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

}
