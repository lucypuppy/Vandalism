/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.util.minecraft;

public class TimerHack {

    private static final float DEFAULT_TIMER_SPEED = 1.0F;

    private static float TIMER_SPEED = DEFAULT_TIMER_SPEED;

    public static void setSpeed(final float speed) {
        TIMER_SPEED = Math.max(0.01F, speed);
    }

    public static float getSpeed() {
        return TIMER_SPEED;
    }

    public static void reset() {
        TIMER_SPEED = DEFAULT_TIMER_SPEED;
    }

}
