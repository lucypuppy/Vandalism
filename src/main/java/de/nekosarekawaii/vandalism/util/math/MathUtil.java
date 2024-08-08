/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util.math;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;

public class MathUtil implements MinecraftWrapper {

    public static final double EXPANDER = 1.6777216E7D;
    public static final long MINIMUM_ROTATION_DIVISOR = 131072L;

    public static double getGcd() {
        final double sensitivity = mc.options.getMouseSensitivity().getValue() * 0.6D + 0.2D;
        final double sensitivityPow3 = sensitivity * sensitivity * sensitivity;
        final double sensitivityPow3Mult8 = sensitivityPow3 * 8.0;

        if (mc.options.getPerspective().isFirstPerson() && mc.player.isUsingSpyglass()) {
            return sensitivityPow3;
        } else {
            return sensitivityPow3Mult8;
        }
    }

    public static float cubicBezier(float p0, float p1, float p2, float p3, float t) {
        final float u = 1 - t;
        final float tt = t * t;
        final float uu = u * u;
        final float uuu = uu * u;
        final float ttt = tt * t;
        return uuu * p0 + 3 * uu * t * p1 + 3 * u * tt * p2 + ttt * p3;
    }

    public static double normalizeGaussian(double gaussian, double mean, double std) {
        final double normalized = (gaussian - mean) / std;
        return sigmoid(normalized);
    }

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static double densityFunction(double x, double mean, double std) {
        final double firstPart = 1.0 / (std * Math.sqrt(2 * Math.PI));
        final double exponent = -Math.pow(x - mean, 2) / (2 * Math.pow(std, 2));
        return firstPart * Math.exp(exponent);
    }

    public static boolean isBetween(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean isBetween(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public static boolean isBetween(float value, float min, float max) {
        return value >= min && value <= max;
    }

    public static boolean isBetween(byte value, byte min, byte max) {
        return value >= min && value <= max;
    }

    public static boolean isBetween(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static long getAbsoluteGcd(final float current, final float last) {
        final long currentExpanded = (long) (current * EXPANDER);
        final long lastExpanded = (long) (last * EXPANDER);
        return gcd(currentExpanded, lastExpanded);
    }

    private static long gcd(final long current, final long last) {
        return (last <= 16384L) ? current : gcd(last, current % last);
    }

}
