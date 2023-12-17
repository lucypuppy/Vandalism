package de.nekosarekawaii.vandalism.util.minecraft;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;

public class MathUtil implements MinecraftWrapper {

    public static double getFixedMinecraftReach(double range) {
        return range * range;
    }

}