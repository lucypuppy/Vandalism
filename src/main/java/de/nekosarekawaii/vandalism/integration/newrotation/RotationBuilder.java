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
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationBuilder implements MinecraftWrapper {

    public static Rotation build(final Vec3d to, final Vec3d eyePos, final RotationPriority priority) {
        final Vec3d diff = to.subtract(eyePos);
        final double hypot = Math.hypot(diff.getX(), diff.getZ());
        final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0f / Math.PI)) - 90.0f;
        final float pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0f / Math.PI));
        return new Rotation(yaw, pitch, priority);
    }

    public static Rotation build(final Vec3d to, final Vec3d eyePos) {
        return build(to, eyePos, RotationPriority.NORMAL);
    }

    public static Rotation build(final Entity entity, final RotationPriority priority, final boolean bestHitVec, final double range) {
        final Vec3d entityVector = bestHitVec ? getNearestPoint(entity) : entity.getPos();
        Rotation normalRotations = build(entityVector, mc.player.getEyePos(), priority);

        // If we can hit the entity with the normal rotations, return them
        if (WorldUtil.raytrace(normalRotations, range).getType() == HitResult.Type.MISS) { // TODO: Maybe add faster ray traces
            return normalRotations;
        }

        final Box bb = entity.getBoundingBox();
        Vec3d currentVector = null;

        for (double x = 0.00D; x < 1.00D; x += 0.1D) {
            for (double y = 0.00D; y < 1.00D; y += 0.1D) {
                for (double z = 0.00D; z < 1.00D; z += 0.1D) {
                    final Vec3d vector3d = new Vec3d(
                            bb.minX + (bb.maxX - bb.minX) * x,
                            bb.minY + (bb.maxY - bb.minY) * y,
                            bb.minZ + (bb.maxZ - bb.minZ) * z);

                    if (mc.player.getEyePos().distanceTo(vector3d) > range)
                        continue;

                    final Rotation parsedRotation = build(vector3d, mc.player.getEyePos(), priority);
                    if (WorldUtil.raytrace(parsedRotation, range).getType() == HitResult.Type.MISS) // TODO: Maybe add faster ray traces
                        continue;

                    if (!bestHitVec) {
                        return parsedRotation;
                    } else if (currentVector == null || mc.player.getEyePos().distanceTo(vector3d) <= mc.player.getEyePos().distanceTo(currentVector)) {
                        currentVector = vector3d;
                        normalRotations = parsedRotation;
                    }
                }
            }
        }

        return normalRotations;
    }

    public static Vec3d getNearestPoint(final Entity entity) {
        final Box boundingBox = entity.getBoundingBox();

        final double nearestX = Math.max(boundingBox.minX, Math.min(entity.getX(), boundingBox.maxX));
        final double nearestZ = Math.max(boundingBox.minZ, Math.min(entity.getZ(), boundingBox.maxZ));

        final double nearestY = entity.getY() + Math.max(0,
                Math.min(mc.player.getY() - entity.getY() + mc.player.getEyeHeight(mc.player.getPose()),
                        (boundingBox.maxY - boundingBox.minY) * 0.9)
        );

        return new Vec3d(nearestX, nearestY, nearestZ);
    }

}
