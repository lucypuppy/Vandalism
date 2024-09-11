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

package de.nekosarekawaii.vandalism.util;

/**
 * A simple timer class.
 */
public class MSTimer {

    /**
     * The time in milliseconds.
     */
    protected long time = System.currentTimeMillis();

    /**
     * Whether the timer is paused or not.
     */
    private boolean paused = false;

    /**
     * The time the timer was paused.
     */
    private long pausedTime = 0L;

    /**
     * Pauses the timer.
     */
    public void pause() {
        if (paused) return;
        pausedTime = getDelta();
        paused = true;
    }

    /**
     * Resumes the timer.
     */
    public void resume() {
        if (!paused) return;
        paused = false;
        pausedTime = 0L;
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        time = System.currentTimeMillis();
    }

    /**
     * @param delay The delay to check.
     * @return Whether the timer has reached the given delay.
     */
    public boolean hasReached(final long delay) {
        return getDelta() >= delay;
    }

    /**
     * @param delay The delay to check.
     * @param reset Whether the timer should be reset after the check.
     * @return Whether the timer has reached the given delay.
     */
    public boolean hasReached(final long delay, final boolean reset) {
        final boolean reached = getDelta() >= delay;
        if (reached && reset) reset();

        return reached;
    }

    /**
     * @return The delta time in milliseconds.
     */
    public long getDelta() {
        return System.currentTimeMillis() - getTime();
    }

    /**
     * @return The time in milliseconds. If the timer is paused, the paused time is returned.
     */
    public long getTime() {
        return paused ? pausedTime : time;
    }

    public void setTime(final long time) {
        this.time = time;
    }

}