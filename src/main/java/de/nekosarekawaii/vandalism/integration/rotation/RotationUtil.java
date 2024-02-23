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

package de.nekosarekawaii.vandalism.integration.rotation;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RotationUtil implements MinecraftWrapper {

    public static List<Byte> getVisibleHitBoxSides(final Entity entity, final PlayerEntity player) {
        final List<Byte> sides = new ArrayList<>();
        final float width = (entity.getWidth() + 0.2f) / 2f;
        final float height = entity.getHeight() + 0.2f;
        final double eyePosY = entity.getY() - 0.1;

        if (player.getZ() < entity.getZ() - width || player.getZ() > entity.getZ() + width) {
            sides.add((byte) 0); //x
        }

        if (player.getX() < entity.getX() - width || player.getX() > entity.getX() + width) {
            sides.add((byte) 1); //z
        }

        if (player.getY() + player.getEyeHeight(player.getPose()) < eyePosY || player.getY() + player.getEyeHeight(player.getPose()) > eyePosY + height) {
            sides.add((byte) 2); //y
        }

        if (player.getBoundingBox().intersects(entity.getBoundingBox())) {
            sides.add((byte) 3); //inside
        }

        return sides;
    }

    public static List<Vec3d> computeHitBoxAimPoints(final Entity entity, final PlayerEntity player, final int aimPoints) {
        final List<Vec3d> points = new ArrayList<>();
        final List<Byte> visibleSides = getVisibleHitBoxSides(entity, player);
        final double targetPosY = entity.getY() - 0.1;
        final double targetHeight = entity.getHeight() + 0.2;
        final double targetWidth = entity.getWidth();

        // If the entity has no width or height we can ignore it to fix the out of memory crash
        if (targetWidth <= 0.0 || targetHeight <= 0.2) {
            return points;
        }

        /*
         * hit box formula:
         * visibleSides * width * height
         * (points * points)
         */
        final double horDist = targetWidth / aimPoints;
        final double vertDist = targetHeight / aimPoints;
        if (visibleSides.contains((byte) 0)) { //x
            for (double y = 0; y <= targetHeight; y += vertDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double zOff = (player.getZ() > entity.getZ() ? targetWidth / 2 : -targetWidth / 2);
                    points.add(new Vec3d(entity.getX() - targetWidth / 2 + x, targetPosY + y, entity.getZ() + zOff));
                }
            }
        }

        if (visibleSides.contains((byte) 2)) { //y
            for (double y = 0; y <= targetWidth; y += horDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double yOff = (player.getEyeY() < targetPosY ? 0 : targetHeight);
                    points.add(new Vec3d(entity.getX() - targetWidth / 2 + x, targetPosY + yOff, entity.getZ() - targetWidth / 2 + y));
                }
            }
        }

        if (visibleSides.contains((byte) 1)) { //z
            for (double y = 0; y <= targetHeight; y += vertDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double xOff = (player.getX() > entity.getX() ? targetWidth / 2 : -targetWidth / 2);
                    points.add(new Vec3d(entity.getX() + xOff, targetPosY + y, entity.getZ() - targetWidth / 2 + x));
                }
            }
        }

        if (visibleSides.contains((byte) 3)) {
            points.add(new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ()));
        }

        Collections.shuffle(points);
        return points;
    }

    //hashmap, collect all damageable points in one list
    public static Vec3d findClosestVisiblePoint(List<Vec3d> aimPoints, Entity e, double range) {
        double tempDist = Double.MAX_VALUE;
        Vec3d closestPoint = null;

        final Vec3d eyes = mc.player.getEyePos();

        for (Vec3d p : aimPoints) {
            final Rotation rotation = Rotation.Builder.build(p, eyes);
            if (!WorldUtil.canHitEntity(mc.player, e, rotation, range))
                continue;

            final double dist = Math.sqrt(Math.pow(p.x - eyes.x, 2.0) + Math.pow(p.y - eyes.y, 2.0) + Math.pow(p.z - eyes.z, 2.0));
            if (dist > 0D && dist < tempDist) {
                tempDist = dist;
                closestPoint = p;
            }
        }
        return closestPoint;
    }

    public static float calculateRotationPercentage(final float currentRotation, final float targetRotation, final boolean yaw) {
        final float diff = MathHelper.angleBetween(currentRotation, targetRotation);
        return 1.0f - (diff / (yaw ? 180.0f : 90.0f));
    }

    // correlation can overaim/underaim, i recomend to set it at around 0.2f
    public static Rotation rotationDistribution(final Rotation rotation, final Rotation lastRotation, final float rotateSpeed, final float correlationStrength) {
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

                return new Rotation(lastYaw + correlatedMoveYaw, lastPitch + correlatedMovePitch);
            }
        }

        return rotation;
    }

    public static boolean isEntityLookingAtEntity(final Entity origin, final Entity target, final double diff) {
        if (target == null || origin == null) return false;
        final Rotation pseudoRotation = Rotation.Builder.build(target.getPos(), origin.getEyePos());
        return Math.abs(MathHelper.wrapDegrees(pseudoRotation.getYaw()) - MathHelper.wrapDegrees(target.getYaw())) > diff;
    }

}
