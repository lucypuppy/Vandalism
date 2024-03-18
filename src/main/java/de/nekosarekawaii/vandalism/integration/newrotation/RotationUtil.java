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

import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class RotationUtil implements MinecraftWrapper {

    private static final double SQRT3 = Math.sqrt(3);
    private static final double SQRT5 = Math.sqrt(5);

    public static float calculateRotationPercentage(final float currentRotation, final float targetRotation, final boolean yaw) {
        final float diff = MathHelper.angleBetween(currentRotation, targetRotation);
        return 1.0f - (diff / (yaw ? 180.0f : 90.0f));
    }

    // correlation can overaim/underaim, i recomend to set it at around 0.2f
    public static Rotation rotationDistribution(Rotation rotation, final Rotation lastRotation, final float rotateSpeed,
                                                final float correlationStrength) {
        if (rotateSpeed > 0) {
            final float lastYaw = lastRotation.getYaw();
            final float lastPitch = lastRotation.getPitch();
            final float deltaYaw = MathHelper.wrapDegrees(rotation.getYaw() - lastYaw);
            final float deltaPitch = rotation.getPitch() - lastPitch;
            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);

            if (distance > 0) {
                final double distributionYaw = Math.abs(deltaYaw / distance);
                final double distributionPitch = Math.abs(deltaPitch / distance);
                final double maxYaw = rotateSpeed * distributionYaw;
                final double maxPitch = rotateSpeed * distributionPitch;

                // Introduce correlation between yaw and pitch
                final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
                final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

                // Apply correlation (reverse the effect)
                float correlatedMoveYaw = moveYaw;
                float correlatedMovePitch = movePitch;

                if (correlationStrength > 0.0f) {
                    correlatedMoveYaw = moveYaw + movePitch * correlationStrength;
                    correlatedMovePitch = movePitch + moveYaw * correlationStrength;
                }

                return new Rotation(lastYaw + correlatedMoveYaw, lastPitch + correlatedMovePitch, rotation.getPriority());
            }
        }

        return rotation;
    }

    public static boolean isEntityLookingAtEntity(final Entity origin, final Entity target, final double diff) {
        if (target == null || origin == null) return false;
        final Rotation pseudoRotation = RotationBuilder.build(target.getPos(), origin.getEyePos());
        return Math.abs(MathHelper.wrapDegrees(pseudoRotation.getYaw()) - MathHelper.wrapDegrees(target.getYaw())) > diff;
    }

    // https://ben.land/post/2021/04/25/windmouse-human-mouse-movement/
    public static Rotation windMouseSmooth(final Rotation rotation, final Rotation lastRotation,
                                           float gravitationalForce, float windForceMagnitude, float maxStepSize,
                                           float distanceThreshold) {
        float currentYaw = lastRotation.getYaw();
        float currentPitch = lastRotation.getPitch();
        float velocityX = 0;
        float velocityY = 0;
        float windX = 0;
        float windY = 0;
        final Random random = new Random();

        final float distance = (float) Math.hypot(rotation.getYaw() - lastRotation.getYaw(),
                rotation.getPitch() - lastRotation.getPitch());

        if (distance >= 1) {
            final float windMagnitude = Math.min(windForceMagnitude, distance);

            if (distance >= distanceThreshold) {
                windX = windX / (float) SQRT3 + (2 * random.nextFloat() - 1) * windMagnitude / (float) SQRT5;
                windY = windY / (float) SQRT3 + (2 * random.nextFloat() - 1) * windMagnitude / (float) SQRT5;
            } else {
                windX /= (float) SQRT3;
                windY /= (float) SQRT3;

                if (maxStepSize < 3) {
                    maxStepSize = random.nextFloat() * 3 + 3;
                } else {
                    maxStepSize /= (float) SQRT5;
                }
            }

            velocityX += windX + gravitationalForce * (rotation.getYaw() - lastRotation.getYaw()) / distance;
            velocityY += windY + gravitationalForce * (rotation.getPitch() - lastRotation.getPitch()) / distance;

            final float velocityMagnitude = (float) Math.hypot(velocityX, velocityY);
            if (velocityMagnitude > maxStepSize) {
                final float velocityClip = maxStepSize / 2.0f + random.nextFloat() * maxStepSize / 2.0f;
                velocityX = (velocityX / velocityMagnitude) * velocityClip;
                velocityY = (velocityY / velocityMagnitude) * velocityClip;
            }

            final float newX = rotation.getYaw() + velocityX;
            final float newY = rotation.getPitch() + velocityY;
            if (currentYaw != newX || currentPitch != newY) {
                currentYaw = newX;
                currentPitch = newY;
            }
        }

        return new Rotation(currentYaw, currentPitch, rotation.getPriority());
    }


}
