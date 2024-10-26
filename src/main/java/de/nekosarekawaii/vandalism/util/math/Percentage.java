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

/**
 * Implementation of a percentage calculator.
 */
public class Percentage {

    /**
     * Calculates the percentage value of a given base.
     *
     * @param percentageDecimal The percentage as decimal.
     * @param base              The base value.
     * @return The percentage value.
     */
    public static float value(final float percentageDecimal, final float base) {
        return percentageDecimal * base * 0.01F; // * 0.01 = : 100
    }

    /**
     * Calculates the base value of a given percentage.
     *
     * @param percentageDecimal The percentage as decimal.
     * @param value             The percentage value.
     * @return The base value.
     */
    public static float base(final float percentageDecimal, final float value) {
        return (value / percentageDecimal) * 100F;
    }

    /**
     * Calculates the percentage of a given value.
     *
     * @param value The percentage value.
     * @param base  The base value.
     * @return The percentage.
     */
    public static float percentage(final float value, final float base) {
        return (value / base) * 100F;
    }

    /**
     * Converts a percentage to a decimal.
     *
     * @param percentage The percentage.
     * @return The decimal.
     */
    public static float toDecimal(final float percentage) {
        return percentage * 0.01F; // * 0.01 = : 100
    }

    /**
     * Converts a decimal to a percentage.
     *
     * @param decimal The decimal.
     * @return The percentage.
     */
    public static float toPercentage(final float decimal) {
        return decimal * 100F;
    }

    /**
     * Calculates the percentage value of a given base.
     *
     * @param percentageDecimal The percentage as decimal.
     * @param base              The base value.
     * @return The percentage value.
     */
    public static double value(final double percentageDecimal, final double base) {
        return percentageDecimal * base * 0.01; // * 0.01 = : 100
    }

    /**
     * Calculates the base value of a given percentage.
     *
     * @param percentageDecimal The percentage as decimal.
     * @param value             The percentage value.
     * @return The base value.
     */
    public static double base(final double percentageDecimal, final double value) {
        return (value / percentageDecimal) * 100D;
    }


    /**
     * Calculates the percentage of a given value.
     *
     * @param value The percentage value.
     * @param base  The base value.
     * @return The percentage.
     */
    public static double percentage(final double value, final double base) {
        return (value / base) * 100D;
    }

    /**
     * Converts a percentage to a decimal.
     *
     * @param percentage The percentage.
     * @return The decimal.
     */
    public static double toDecimal(final double percentage) {
        return percentage * 0.01D; // * 0.01 = : 100
    }

    /**
     * Converts a decimal to a percentage.
     *
     * @param decimal The decimal.
     * @return The percentage.
     */
    public static double toPercentage(final double decimal) {
        return decimal * 100D;
    }

}