/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

public class Boundings {

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are relative.
     */
    public static boolean isInBounds(final short x, final short y, final short left, final short up, final short right, final short down) {
        return x >= left && x <= left + right && y >= up && y <= up + down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are relative.
     */
    public static boolean isInBounds(final int x, final int y, final int left, final int up, final int right, final int down) {
        return x >= left && x <= left + right && y >= up && y <= up + down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are relative.
     */
    public static boolean isInBounds(final long x, final long y, final long left, final long up, final long right, final long down) {
        return x >= left && x <= left + right && y >= up && y <= up + down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are relative.
     */
    public static boolean isInBounds(final float x, final float y, final float left, final float up, final float right, final float down) {
        return x >= left && x <= left + right && y >= up && y <= up + down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are relative.
     */
    public static boolean isInBounds(final double x, final double y, final double left, final double up, final double right, final double down) {
        return x >= left && x <= left + right && y >= up && y <= up + down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are absolute.
     */
    public static boolean isInBoundsAbsolute(final short x, final short y, final short left, final short up, final short right, final short down) {
        return x >= left && x <= right && y >= up && y <= down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are absolute.
     */
    public static boolean isInBoundsAbsolute(final int x, final int y, final int left, final int up, final int right, final int down) {
        return x >= left && x <= right && y >= up && y <= down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are absolute.
     */
    public static boolean isInBoundsAbsolute(final long x, final long y, final long left, final long up, final long right, final long down) {
        return x >= left && x <= right && y >= up && y <= down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are absolute.
     */
    public static boolean isInBoundsAbsolute(final float x, final float y, final float left, final float up, final float right, final float down) {
        return x >= left && x <= right && y >= up && y <= down;
    }

    /**
     * @param x     The x value.
     * @param y     The y value.
     * @param left  The left value.
     * @param up    The up value.
     * @param right The right value.
     * @param down  The down value.
     * @return Whether the given point is in the bounds of the given rectangle. The bounds are absolute.
     */
    public static boolean isInBoundsAbsolute(final double x, final double y, final double left, final double up, final double right, final double down) {
        return x >= left && x <= right && y >= up && y <= down;
    }

}