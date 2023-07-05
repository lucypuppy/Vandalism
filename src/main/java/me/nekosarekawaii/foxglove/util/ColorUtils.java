package me.nekosarekawaii.foxglove.util;

import java.awt.*;

public class ColorUtils {

    public static Color mixColors(final Color color1, final Color color2, final float percent) {
        final double inverse_percent = 1.0 - percent;
        final int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        final int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        final int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public static Color mixColors(final Color color1, final Color color2, final Color color3, final float percent) {
        if (percent <= 0.5) return ColorUtils.mixColors(color2, color3, percent * 2f);
        return ColorUtils.mixColors(color1, color2, (percent - 0.5f) * 2f);
    }

}