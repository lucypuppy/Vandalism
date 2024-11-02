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

package de.nekosarekawaii.vandalism.integration.rotation;

import lombok.Data;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Data
public class Rotation {

    private float yaw, pitch;

    public Rotation(final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vec3d getVec() {
        return Vec3d.fromPolar(pitch, yaw);
    }

    public String toString(final Vec2f vec) {
        return "Vec2f[" + vec.x + ", " + vec.y + "]";
    }

    @Override
    public String toString() {
        return "Rotation[yaw=" + yaw + ", pitch=" + pitch + "]";
    }

}
