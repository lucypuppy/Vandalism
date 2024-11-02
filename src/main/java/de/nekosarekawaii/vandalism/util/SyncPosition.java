/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

public class SyncPosition extends TrackedPosition {

    private Vec3d newPos;
    private int lerpSteps;

    public SyncPosition(Vec3d pos) {
        super();
        this.pos = pos;
        this.newPos = Vec3d.ZERO;
    }

    public SyncPosition() {
        this(Vec3d.ZERO);
    }

    public void setPos(Vec3d pos, boolean lerp) {
        if (lerp) {
            this.newPos = pos;
            this.lerpSteps = 3;
            return;
        }

        this.pos = pos;
        this.newPos = pos;
        this.lerpSteps = 0;
    }

    public void onLivingUpdate() {
        if (this.lerpSteps > 0) {
            final double x = this.pos.x + (this.newPos.x - this.pos.x) / (double) this.lerpSteps;
            final double y = this.pos.y + (this.newPos.y - this.pos.y) / (double) this.lerpSteps;
            final double z = this.pos.z + (this.newPos.z - this.pos.z) / (double) this.lerpSteps;
            --this.lerpSteps;

            this.pos = new Vec3d(x, y, z);
        }
    }

}
