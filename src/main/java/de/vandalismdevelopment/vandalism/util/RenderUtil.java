package de.vandalismdevelopment.vandalism.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
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

    public static int interpolateColor(final Color minColor, final Color maxColor, final double percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (minColor.getRed() * inverse_percent + maxColor.getRed() * percent);
        final int greenPart = (int) (minColor.getGreen() * inverse_percent + maxColor.getGreen() * percent);
        final int bluePart = (int) (minColor.getBlue() * inverse_percent + maxColor.getBlue() * percent);
        final int alphaPart = (int) (minColor.getAlpha() * inverse_percent + maxColor.getAlpha() * percent);
        return rgbaToValue(redPart, greenPart, bluePart, alphaPart);
    }

    public static int interpolateColor(final Color minColor, final Color midColor, final Color maxColor, final double percent) {
        if (minColor == null || midColor == null || maxColor == null) {
            throw new IllegalArgumentException("Color can't be null.");
        }
        if (percent <= 0.5) {
            return interpolateColor(minColor, midColor, percent * 2D);
        }
        return interpolateColor(midColor, maxColor, (percent - 0.5) * 2D);
    }

    public static int rgbaToValue(final int r, final int g, final int b, final int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }

    public static int rgbaToValueFloat(final float r, final float g, final float b, final float a) {
        return rgbaToValue((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5), (int) (a * 255 + 0.5));
    }

    public static int[] valueToRGBA(final int value) {
        return new int[]{((value >> 16) & 0xFF), ((value >> 8) & 0xFF), (value & 0xFF), ((value >> 24) & 0xFF)};
    }

    public static Color withAlpha(final Color color, final int alpha) {
        if (color == null) throw new IllegalArgumentException("Color can't be null.");
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static float[] withAlpha(final float[] color, final float alpha) {
        if (color.length != 3) throw new IllegalArgumentException("Color array must have 3 elements.");
        return new float[]{color[0], color[1], color[2], alpha};
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
            final int color = interpolateColor(color1, color2, i / (textLength - 1.0));
            newText.append(Text.literal(String.valueOf(text.charAt(i))).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
        }
        return newText;
    }

}
