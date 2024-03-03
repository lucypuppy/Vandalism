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

import net.minecraft.entity.TrackedPosition;
import net.minecraft.util.math.Vec3d;

public class NetworkEntity {

    public final TrackedPosition trackedPosition = new TrackedPosition();
    public Vec3d pos, lastPos;
    public int lerpSteps;

    public NetworkEntity() {
        this.pos = new Vec3d(0, 0, 0);
        this.lastPos = new Vec3d(0, 0, 0);
    }

    public void setPosLerp(Vec3d pos) {
        this.trackedPosition.pos = pos;

        if (this.pos == null) {
            this.pos = pos;
        }

        this.lerpSteps = 3; // This is 3 in the minecraft code so i also hardcode this
    }

    public void onLivingUpdate() {
        if (this.lerpSteps > 0 && this.pos != null && this.trackedPosition.pos != null) {
            final Vec3d newPos = this.trackedPosition.pos;
            final double x = this.pos.x + (newPos.x - this.pos.x) / (double) this.lerpSteps;
            final double y = this.pos.y + (newPos.y - this.pos.y) / (double) this.lerpSteps;
            final double z = this.pos.z + (newPos.z - this.pos.z) / (double) this.lerpSteps;
            --this.lerpSteps;

            this.pos = new Vec3d(x, y, z);
        }
    }

}
