package me.nekosarekawaii.foxglove.util.minecraft;

import net.minecraft.util.Formatting;

import java.util.concurrent.ThreadLocalRandom;

public class FormattingUtils {

    public static Formatting getRandomColor() {
        return Formatting.values()[ThreadLocalRandom.current().nextInt(1, 14)];
    }

}
