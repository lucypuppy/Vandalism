package me.nekosarekawaii.foxglove.util.render;

import java.awt.*;

public class ColorUtils {

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