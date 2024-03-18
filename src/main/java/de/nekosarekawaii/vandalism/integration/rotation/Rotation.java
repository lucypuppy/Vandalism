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

package de.nekosarekawaii.vandalism.integration.rotation;

import de.nekosarekawaii.vandalism.integration.rotation.enums.HitBoxSelectMode;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Rotation implements MinecraftWrapper {

    private float yaw, pitch;

    private Vec3d targetVector;

    public Rotation(final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setTargetVector(final Vec3d vector) {
        this.targetVector = vector;
    }

    public Vec3d getTargetVector() {
        return this.targetVector;
    }

    public Vec3d getVector() {
        return Vec3d.fromPolar(pitch, yaw);
    }

    @Override
    public String toString() {
        return "{" + "yaw=" + this.yaw + ", pitch=" + this.pitch + '}';
    }

    public static class Builder {

        private static Vec3d lastPoint;

        public static Rotation build(Entity e, double range, int saimPoints, HitBoxSelectMode mode) {
            final List<Vec3d> aimPoints = RotationUtil.computeHitBoxAimPoints(e, mc.player, saimPoints);
            final Vec3d eyePos = mc.player.getEyePos();

            // Create an instance of AimPointMover with your aimPoints
            final AimPointMover aimPointMover = new AimPointMover(mode, 0.1, 0.6);

            // Get the current best point
            Vec3d bestPoint = RotationUtil.findClosestVisiblePoint(aimPoints, e, range);

            if (bestPoint == null) { // Sanitycheck
                return null;
            }

            // Define your desired range (e.g., 3.0) here
            final double desiredRange = 3.0;

            // Calculate the distance to the best point
            final double currentDistance = bestPoint.distanceTo(eyePos);

            // If the best point is within range, move to the next smooth point
            bestPoint = aimPointMover.getNextSmoothPointTest(bestPoint, lastPoint, currentDistance > desiredRange ? 6 : 3, range, e);
            if (bestPoint != null) {
                lastPoint = bestPoint;
            }

            // Calculate the rotation towards the updated best point
            return build(bestPoint, eyePos);
        }

        public static Rotation build(final Vec3d to, final Vec3d eyePos) {
            final Vec3d diff = to.subtract(eyePos);
            final double hypot = Math.hypot(diff.getX(), diff.getZ());
            final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0f / Math.PI)) - 90.0f;
            final float pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0f / Math.PI));
            return new Rotation(yaw, pitch);
        }

    }

}
