/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util.common;

public class MathUtil {

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

}
