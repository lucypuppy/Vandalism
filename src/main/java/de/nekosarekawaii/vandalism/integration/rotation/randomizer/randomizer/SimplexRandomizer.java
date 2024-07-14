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

package de.nekosarekawaii.vandalism.integration.rotation.randomizer.randomizer;

import de.nekosarekawaii.vandalism.integration.rotation.randomizer.Randomizer;
import de.nekosarekawaii.vandalism.util.SimplexNoise;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;

@Setter
public class SimplexRandomizer extends Randomizer {

    private final long startTime;
    private double maxRadius;
    private double maxRadiusY;
    private double mindDistance;

    public SimplexRandomizer() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public Vec3d randomiseRotationVec3d(Vec3d vec3d) {
        final double time = (System.currentTimeMillis() - startTime) / 200.0;

        final double jitterX = this.maxRadius * SimplexNoise.noise(time, 0, 0);
        final double jitterY = this.maxRadiusY * SimplexNoise.noise(0, time, 0);
        final double jitterZ = this.maxRadius * SimplexNoise.noise(0, 0, time);

        final Vec3d newVec = new Vec3d(vec3d.x + jitterX, vec3d.y + jitterY, vec3d.z + jitterZ);

        if (this.mindDistance <= 0) {
            return newVec;
        }

        final double dist = vec3d.distanceTo(newVec);
        if (dist < this.mindDistance) {
            final double moveFactor = 0.1 + (this.mindDistance / dist);
            final double newX = vec3d.getX() + (newVec.getX() - vec3d.getX()) * moveFactor;
            final double newY = vec3d.getY() + (newVec.getY() - vec3d.getY()) * moveFactor;
            final double newZ = vec3d.getZ() + (newVec.getZ() - vec3d.getZ()) * moveFactor;
            return new Vec3d(newX, newY, newZ);
        }

        return newVec;
    }

    @Override
    public String getName() {
        return "Simplex";
    }

}
