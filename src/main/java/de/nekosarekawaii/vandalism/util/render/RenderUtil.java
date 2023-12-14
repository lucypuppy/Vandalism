package de.nekosarekawaii.vandalism.util.render;

import de.florianmichael.rclasses.common.ColorUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class RenderUtil {

    private static double FPS, PREV_GL_TIME = Double.NaN;

    public static void drawFrame() {
        if (Double.isNaN(PREV_GL_TIME)) {
            PREV_GL_TIME = GLFW.glfwGetTime();
            return;
        }
        final double time = GLFW.glfwGetTime();
        FPS = 1.0 / (time - PREV_GL_TIME);
        PREV_GL_TIME = time;
    }

    public static double getFps() {
        return FPS;
    }

    public static Color interpolateColor(final Color minColor, final Color midColor, final Color maxColor, final double percent) {
        if (minColor == null || midColor == null || maxColor == null) {
            throw new IllegalArgumentException("Color can't be null.");
        }
        if (percent <= 0.5) {
            return ColorUtils.colorInterpolate(minColor, midColor, percent * 2D);
        }
        return ColorUtils.colorInterpolate(midColor, maxColor, (percent - 0.5) * 2D);
    }

    public static Formatting getRandomColor() {
        return Formatting.values()[ThreadLocalRandom.current().nextInt(1, 14)];
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

}
