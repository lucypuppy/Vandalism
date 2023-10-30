package de.vandalismdevelopment.vandalism.util;

import de.vandalismdevelopment.vandalism.util.render.ColorUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class FormattingUtil {

    public static Formatting getRandomColor() {
        return Formatting.values()[ThreadLocalRandom.current().nextInt(1, 14)];
    }

    public static MutableText interpolateTextColor(final String text, final Color color1, final Color color2) {
        final MutableText newText = Text.empty();
        final int textLength = text.length();

        for (int i = 0; i < textLength; i++) {
            final int color = ColorUtil.interpolate(color1, color2, i / (textLength - 1.0));
            newText.append(Text.literal(String.valueOf(text.charAt(i))).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
        }

        return newText;
    }

}
