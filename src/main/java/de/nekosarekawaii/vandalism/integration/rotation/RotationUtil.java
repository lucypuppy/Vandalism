/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements MinecraftWrapper {

    public static PrioritizedRotation rotationToEntity(final LivingEntity entity, final RotationPriority priority) {
        Vec3d eyePos = mc.player.getEyePos();
        return getRotation(entity.getPos(), eyePos, priority);
    }

    public static PrioritizedRotation rotationToVec(final Vec3d vec, final RotationPriority priority) {
        Vec3d eyePos = mc.player.getEyePos();
        return getRotation(vec, eyePos, priority);
    }

    private static PrioritizedRotation getRotation(final Vec3d to, final Vec3d eyePos, final RotationPriority priority) {
        final Vec3d diff = to.subtract(eyePos);
        final double hypot = Math.hypot(diff.getX(), diff.getZ());
        final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0f / Math.PI)) - 90.0f;
        final float pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0f / Math.PI));
        return new PrioritizedRotation(yaw, pitch, priority);
    }

    public static PrioritizedRotation rotateMouse(final PrioritizedRotation desiredRotation, final Rotation prevRotation, final double rotationSpeed, final double deltaTime, final boolean didRotate) {
        final float desiredYaw = desiredRotation.getYaw();
        final float desiredPitch = desiredRotation.getPitch();
        final float prevYaw = prevRotation.getYaw();
        final float prevPitch = prevRotation.getPitch();

        final float timeFactor = (float) (deltaTime / 20.0f);

        final float f = (float) (mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F);
        final float onePixel = f * f * f * 8.0F;
        final float yawDiff = MathHelper.wrapDegrees(desiredYaw - prevYaw);
        final float pitchDiff = desiredPitch - prevPitch;

        float speed = (float) rotationSpeed / 8.0f;

        final double distance = Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
        speed *= 1.0f + (float) distance / 45.0f; // Increase speed based on the largest difference

        float accelerationPhaseTime = 0.35f;  // The duration of the acceleration phase in seconds (adjust as necessary)
        float accelerationFactor = Math.min(timeFactor / accelerationPhaseTime, 1.0f);  // Cap acceleration factor at 1.0 (100%)3
        System.out.println(accelerationFactor);
        speed *= 0.5f + accelerationFactor * 0.5f;  // Starts at half speed and accelerates to full speed

        // Apply deceleration if within the threshold
        float decelerationThreshold = 5.0f; // Threshold angle to start decelerating
        if (Math.abs(yawDiff) < decelerationThreshold) {
            float decelerationFactor = 0.08f; // Adjust this factor to change the deceleration rate
            speed *= decelerationFactor;
        }

        // slowdown rotation in frame to keep rotating until all frames are finished until next tick, so the pitch reaches its destination in the same time as the yaw
        // Adjust speed multipliers to synchronize yaw and pitch changes
        float yawSpeedMult = 1;
        float pitchSpeedMult = 1;
        if (Math.abs(yawDiff) > Math.abs(pitchDiff)) {
            pitchSpeedMult = Math.abs(pitchDiff) / Math.abs(yawDiff);
        } else if (Math.abs(pitchDiff) > Math.abs(yawDiff)) {
            yawSpeedMult = Math.abs(yawDiff) / Math.abs(pitchDiff);
        }

        // Calculate allowed yaw and pitch changes
        float allowedYawChange = yawDiff > 0
                ? Math.min(speed, yawDiff)  // Limit yaw change to the smaller of speed or yaw difference if positive
                : Math.max(-speed, yawDiff); // Limit yaw change to the larger of -speed or yaw difference if negative
        float allowedPitchChange = pitchDiff > 0
                ? Math.min(speed, pitchDiff)  // Limit pitch change to the smaller of speed or pitch difference if positive
                : Math.max(-speed, pitchDiff); // Limit pitch change to the larger of -speed or pitch difference if negative

        allowedYawChange *= timeFactor;
        allowedPitchChange *= timeFactor;

        // Calculate final yaw and pitch changes
        final float maxYaw = allowedYawChange * yawSpeedMult / 0.15F;
        final float maxPitch = allowedPitchChange * pitchSpeedMult/ 0.15F;

        // Convert changes to pixels
        final int yawPixels = Math.round(maxYaw / onePixel);
        final int pitchPixels = Math.round(maxPitch / onePixel);

        // Apply the calculated changes
        final float yaw = prevYaw + onePixel * yawPixels * 0.15F;
        final float pitch = MathHelper.clamp(prevPitch + onePixel * pitchPixels * 0.15F, -90.0F, 90.0F);
        return new PrioritizedRotation(yaw, pitch, desiredRotation.getPriority());
    }

    public static boolean isEntityLookingAtEntity(final Entity origin, final Entity target, final double diff) {
        if (target == null || origin == null) return false;
        final PrioritizedRotation pseudoRotation = rotationToVec(target.getPos(), RotationPriority.NORMAL);
        return Math.abs(MathHelper.wrapDegrees(pseudoRotation.getYaw()) - MathHelper.wrapDegrees(target.getYaw())) > diff;
    }

    public static Vec3d clampHitpointsToBoundingBox(final Vec3d vec3d, final Box box, final double shrink) {
        final Box betterBox = box.shrink(shrink, shrink, shrink);
        final double x = MathHelper.clamp(vec3d.x, betterBox.minX, betterBox.maxX);
        final double y = MathHelper.clamp(vec3d.y, betterBox.minY, betterBox.maxY);
        final double z = MathHelper.clamp(vec3d.z, betterBox.minZ, betterBox.maxZ);
        return new Vec3d(x, y, z);
    }

}
