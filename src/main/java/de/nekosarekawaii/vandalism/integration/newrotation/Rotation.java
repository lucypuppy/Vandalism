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

package de.nekosarekawaii.vandalism.integration.newrotation;

import de.nekosarekawaii.vandalism.integration.newrotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;

@Setter
@Getter
public class Rotation implements MinecraftWrapper {

    private float yaw, pitch;
    private RotationPriority priority;

    public Rotation(final float yaw, final float pitch, final RotationPriority priority) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.priority = priority;
    }

    public Rotation(final float yaw, final float pitch) {
        this(yaw, pitch, RotationPriority.NORMAL);
    }

    public Vec3d getVector() {
        return Vec3d.fromPolar(pitch, yaw);
    }

    @Override
    public String toString() {
        return "{" + "yaw=" + this.yaw + ", pitch=" + this.pitch + '}';
    }
}