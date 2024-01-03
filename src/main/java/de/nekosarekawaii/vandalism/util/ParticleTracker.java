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

package de.nekosarekawaii.vandalism.util;

import de.florianmichael.rclasses.math.integration.MSTimer;

public class ParticleTracker {

    private final String particleId;
    private final MSTimer timer;
    private int count;

    public ParticleTracker(final String particleId) {
        this.particleId = particleId;
        this.timer = new MSTimer();
        this.count = 1;
    }

    public String getParticleId() {
        return this.particleId;
    }

    public void increaseCount() {
        this.count++;
    }

    public void resetCount() {
        this.count = 1;
    }

    public MSTimer getTimer() {
        return this.timer;
    }

    public int getCount() {
        return this.count;
    }

}
