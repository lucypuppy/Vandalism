package de.vandalismdevelopment.vandalism.util.render;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.awt.*;

public class ColorUtil {

    public static int interpolate(final Color minColor, final Color maxColor, final double percent) {
        final double inverse_percent = 1.0 - percent;

        final int redPart = (int) (minColor.getRed() * inverse_percent + maxColor.getRed() * percent);
        final int greenPart = (int) (minColor.getGreen() * inverse_percent + maxColor.getGreen() * percent);
        final int bluePart = (int) (minColor.getBlue() * inverse_percent + maxColor.getBlue() * percent);
        final int alphaPart = (int) (minColor.getAlpha() * inverse_percent + maxColor.getAlpha() * percent);

        return rgbaToValue(redPart, greenPart, bluePart, alphaPart);
    }

    public static int interpolate(final Color minColor, final Color midColor, final Color maxColor, final double percent) {
        if (percent <= 0.5)
            return interpolate(minColor, midColor, percent * 2D);

        return interpolate(midColor, maxColor, (percent - 0.5) * 2D);
    }

    public static int rgbaToValue(final int r, final int g, final int b, final int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
    }

    public static int rgbaToValueFloat(final float r, final float g, final float b, final float a) {
        return rgbaToValue((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5), (int) (a * 255 + 0.5));
    }

    public static int[] valueToRGBA(final int value) {
        return new int[]{
                ((value >> 16) & 0xFF),
                ((value >> 8) & 0xFF),
                (value & 0xFF),
                ((value >> 24) & 0xFF)
        };
    }

    public static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static float[] withAlpha(final float[] color, final float alpha) {
        return new float[]{color[0], color[1], color[2], alpha};
    }

    public static float[] getShulkerColor(final ItemStack shulkerItem) {
        final DyeColor dye = ((ShulkerBoxBlock) ShulkerBoxBlock.getBlockFromItem(shulkerItem.getItem())).getColor();

        if (dye == null)
            return new float[]{1f, 1f, 1f};

        final float[] colors = dye.getColorComponents();
        return new float[]{colors[0], colors[1], colors[2]};
    }

}