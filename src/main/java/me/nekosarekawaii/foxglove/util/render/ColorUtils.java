package me.nekosarekawaii.foxglove.util.render;

import java.awt.*;

public class ColorUtils {

    public static int interpolate(final float progress, final Color[] colors, final float[] steps) {
        if (colors.length != steps.length)
            throw new IllegalArgumentException("Colors and steps must have the same length!");

        if (colors.length == 0)
            throw new IllegalArgumentException("Colors and steps must have a length greater than 0!");

        if (progress < 0.0f || progress > 1.0f)
            throw new IllegalArgumentException("Progress must be between 0 and 1!");

        if (progress == 0.0f)
            return colors[0].getRGB();

        if (progress == 1.0f)
            return colors[colors.length - 1].getRGB();

        int left = 0;
        int right = steps.length - 1;
        int mid;

        while (left < right) {
            mid = (left + right) / 2;

            if (progress >= steps[mid]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        if (left == 0) {
            return colors[0].getRGB();
        } else if (left == steps.length) {
            return colors[colors.length - 1].getRGB();
        } else {
            final float stepProgress = (progress - steps[left - 1]) / (steps[left] - steps[left - 1]);
            return interpolate(colors[left - 1], colors[left], stepProgress);
        }
    }

    public static int interpolate(final Color color1, final Color color2, final double percent) {
        final double inverse_percent = 1.0 - percent;

        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        final int alphaPart = (int) (color1.getAlpha() * percent + color2.getAlpha() * inverse_percent);

        return rgbToValue(redPart, greenPart, bluePart, alphaPart);
    }

    public static int interpolate(final Color color1, final Color color2, final Color color3, final double percent) {
        if (percent <= 0.5)
            return interpolate(color2, color3, percent * 2D);

        return interpolate(color1, color2, (percent - 0.5) * 2D);
    }

    public static int rgbToValue(final int r, final int g, final int b, final int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
    }

}