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

package de.nekosarekawaii.vandalism.integration.rotation;

import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RotationUtil implements MinecraftWrapper {

    private static final List<Integer> DIAGONAL_VALUES = Arrays.asList(45, 135, 225, 315);

    public static float calculateRotationPercentage(final float currentRotation, final float targetRotation, final boolean yaw) {
        final float diff = MathHelper.angleBetween(currentRotation, targetRotation);
        return 1.0f - (diff / (yaw ? 180.0f : 90.0f));
    }

    //  correlation can overaim/underaim, i recomend to set it at around 0.2f
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

                //  Introduce correlation between yaw and pitch
                final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
                final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

                final float correlatedMoveYaw;
                final float correlatedMovePitch;
                if (correlationStrength > 0.0f) {
                    correlatedMoveYaw = moveYaw + movePitch * correlationStrength;
                    correlatedMovePitch = movePitch + moveYaw * correlationStrength;
                } else {
                    correlatedMoveYaw = moveYaw;
                    correlatedMovePitch = movePitch;
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

    private static float breathingBaseAmplitude;
    private static float breathingBaseFrequency;
    private static float breathingAmplitude;
    private static float breathingFrequency;
    private static long breathingChangeTime;
    private static final Random random = new Random();

    private static void updateBreathingParameters() {
        breathingAmplitude = breathingBaseAmplitude + (random.nextFloat() - 0.5f) * 0.2f; // Slight random variation
        breathingFrequency = breathingBaseFrequency + (random.nextFloat() - 0.5f) * 0.2f; // Slight random variation
        breathingChangeTime = System.currentTimeMillis() + 1000 + random.nextInt(4000); // Change every 1-5 seconds
    }

    public static Rotation applyBreatheEffect(final Rotation rotation, final float breathingBaseAmplitude, final float breathingBaseFrequency) {
        RotationUtil.breathingBaseAmplitude = breathingBaseAmplitude;
        RotationUtil.breathingBaseFrequency = breathingBaseFrequency;
        // Update breathing parameters periodically
        if (System.currentTimeMillis() >= breathingChangeTime) {
            updateBreathingParameters();
        }
        float pitch = rotation.getPitch() + MathHelper.sin((float) (System.currentTimeMillis() % 1000) / 1000.0f * (float) Math.PI * 2 * breathingFrequency) * breathingAmplitude;
        return new Rotation(rotation.getYaw(), pitch);
    }

    public static boolean isLookingDiagonal(final float rotationYaw) {
        return DIAGONAL_VALUES.contains(getNearestDegree(rotationYaw));
    }

    public static int getNearestDegree(final float rotationYaw) {
        final float wrappedYaw = MathHelper.wrapDegrees(rotationYaw);
        final int nearestDegree = Math.round(wrappedYaw / 45) * 45;
        return Math.floorMod(nearestDegree, 360);
    }

}
