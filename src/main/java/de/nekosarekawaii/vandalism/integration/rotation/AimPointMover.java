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
import de.nekosarekawaii.vandalism.util.game.WorldUtil;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AimPointMover implements MinecraftWrapper {

    private Vec3d bestPoint;
    private final double minRadius, maxRadius;
    private final HitBoxSelectMode mode;

    public AimPointMover(final HitBoxSelectMode mode, final double minRadius, final double maxRadius) {
        this.mode = mode;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
    }

    public Vec3d getNextSmoothPointTest(Vec3d bestPoint, Vec3d lastPoint, double desiredRange, double range, Entity entity) {
        Vec3d rotationPoint = bestPoint;
        this.bestPoint = bestPoint;

        final List<Vec3d> hitablePointsInCircle = generatePointsAround(
                this.bestPoint,
                entity,
                750,
                desiredRange,
                range
        );

        if (hitablePointsInCircle.size() >= 2) {
            if (lastPoint == null) {
                hitablePointsInCircle.sort(Comparator.comparingDouble(bestPoint::distanceTo));
            } else {
                hitablePointsInCircle.sort(Comparator.comparingDouble(lastPoint::distanceTo));
            }

            // Let the point travel around in the circle generated around the besthitvec and let ot travel smoothly
            rotationPoint = hitablePointsInCircle.get(0);
        }

        return rotationPoint;
    }

    private List<Vec3d> generatePointsAround(Vec3d center, Entity entity, int numPoints, double desiredRange, double range) {
        final List<Vec3d> points = new ArrayList<>();
        final double currentDistance = bestPoint.distanceTo(mc.player.getEyePos());
        final double dynamicWidth = Math.min(this.maxRadius, this.minRadius + (this.maxRadius - this.minRadius) * (currentDistance / range));
        final double dynamicHeight = Math.min(this.maxRadius, this.minRadius + (this.maxRadius - this.minRadius) * (currentDistance / range));
        final double goldenAngleIncrement = Math.PI * (1 - Math.sqrt(15));

        for (int i = 0; i < numPoints; i++) {
            double x = center.x;
            double y = center.y;
            double z = center.z;

            switch (this.mode) {
                case CIRCULAR: //  Circular
                    double azimuthalAngle = i * goldenAngleIncrement;
                    double polarAngle = Math.acos(2 * Math.random() - 1); //  Random polar angle,
                    x = center.x + dynamicWidth * Math.sin(polarAngle) * Math.cos(azimuthalAngle);
                    y = center.y + dynamicWidth * Math.sin(polarAngle) * Math.sin(azimuthalAngle);
                    z = center.z + dynamicWidth * Math.cos(polarAngle);
                    break;
                case SQUARE: //  Square
                    x = center.x + dynamicWidth * (Math.random() - 0.5);
                    y = center.y + dynamicHeight * (Math.random() - 0.5);
                    z = center.z + dynamicWidth * (Math.random() - 0.5);
                    break;
            }

            if (!entity.getBoundingBox().contains(x, y, z))
                continue;

            final Vec3d newPoint = new Vec3d(x, y, z);
            final Rotation rotation = Rotation.Builder.build(newPoint, mc.player.getEyePos());
            if (!WorldUtil.canHitEntity(mc.player, entity, rotation, range))
                continue;

            final boolean allowOutOfHitBox = range > desiredRange;
            final double dist = getRaytraceReach(entity, newPoint, range);

            if ((dist > 0 && dist <= range) || allowOutOfHitBox) {
                points.add(newPoint);
            }
        }

        return points;
    }

    private double getRaytraceReach(Entity target, Vec3d point, double reach) {
        final Vec3d eyePos = mc.player.getEyePos();
        final Vec3d rotationVec = Rotation.Builder.build(point, eyePos).getVector();
        final Vec3d maxVec = eyePos.add(rotationVec.x * reach, rotationVec.y * reach, rotationVec.z * reach);

        final Optional<Vec3d> raycast = target.getBoundingBox().raycast(eyePos, maxVec);
        return raycast.map(eyePos::distanceTo).orElse(-1.0);
    }

}
