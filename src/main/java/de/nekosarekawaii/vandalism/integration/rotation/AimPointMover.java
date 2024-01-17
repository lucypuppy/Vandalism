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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AimPointMover implements MinecraftWrapper {

    private Vec3d bestPoint;
    private final List<Vec3d> pointsInRange;
    private double minXZRadius, maxXZRadius, minYRadius, maxYRadius;

    public void setRandomization(double minXZRadius, double maxXZRadius, double minYRadius, double maxYRadius) {
        this.minXZRadius = minXZRadius;
        this.maxXZRadius = maxXZRadius;
        this.minYRadius = minYRadius;
        this.maxYRadius = maxYRadius;
    }

    public AimPointMover(List<Vec3d> aimPoints) {
        this.pointsInRange = new ArrayList<>();
    }

    public Vec3d getNextSmoothPointTest(Vec3d bestPoint, Vec3d lastPoint, double desiredRange, double fixedRange, Entity entity) {
        final Box boundingBox = entity.getBoundingBox();
        Vec3d rotationPoint = bestPoint;

        this.bestPoint = bestPoint;
        this.pointsInRange.clear();

        final List<Vec3d> hitablePointsInCircle = generatePointsAround(
                this.bestPoint,
                entity,
                boundingBox,
                750,
                desiredRange,
                fixedRange
        );

        if (hitablePointsInCircle.size() >= 2) {
            if (lastPoint == null) {
                hitablePointsInCircle.sort(Comparator.comparingDouble(bestPoint::distanceTo));
            } else {
                hitablePointsInCircle.sort(Comparator.comparingDouble(lastPoint::distanceTo));
            }

            //Let the point travel around in the circle generated around the besthitvec and let ot travel smoothly
            final Vec3d firstPoint = hitablePointsInCircle.get(0);
            rotationPoint = firstPoint;
        }

        return rotationPoint;
    }

    private List<Vec3d> generatePointsAround(Vec3d center, Entity target, Box boundingBox, int numPoints, double range, double fixedRange) {
        List<Vec3d> points = new ArrayList<>();
        //List<Vec3> outOfRangePoints = new ArrayList<>();

        for (int i = 0; i < numPoints; i++) {
            double theta = Math.random() * 2 * Math.PI; // Random azimuthal angle
            double phi = Math.acos(2 * Math.random() - 1); // Random polar angle

            double x = center.x + this.maxXZRadius * Math.sin(phi) * Math.cos(theta);
            double y = center.y + this.maxYRadius * Math.sin(phi) * Math.sin(theta);
            double z = center.z + this.maxXZRadius * Math.cos(phi);
            final Vec3d newPoint = new Vec3d(x, y, z);

            final BlockHitResult blockHitResult = WorldUtil.rayTraceBlock(newPoint, range);
            if (blockHitResult != null && blockHitResult.getType() == HitResult.Type.BLOCK)
                continue;

            //range = bestHitVectorPoint Range
            final boolean allowOutOfHitbox = range > fixedRange;
            if (isPointInBoundingBox(newPoint, boundingBox) || allowOutOfHitbox) {
                double dist = getRaytraceReach(target, newPoint, range);
                if ((dist > 0 && dist <= range) || allowOutOfHitbox) {
                    //Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + dist));
                    points.add(newPoint);
                } else {
                    //outOfRangePoints.add(newPoint);
                }
            }
        }

        return points; //new List[]{points/*, outOfRangePoints*/}; @Keksbyte if you wanna do this please use a pair. _FooFieOwO
    }

    private static boolean isPointInBoundingBox(Vec3d point, Box boundingBox) {
        final double halfSize = 0.1; // Adjust this value to control the size of the bounding box
        final double minX = point.x - halfSize;
        final double minY = point.y - halfSize;
        final double minZ = point.z - halfSize;
        final double maxX = point.x + halfSize;
        final double maxY = point.y + halfSize;
        final double maxZ = point.z + halfSize;
        return boundingBox.intersects(new Box(minX, minY, minZ, maxX, maxY, maxZ));

    }

    private double getRaytraceReach(Entity target, Vec3d point, double reach) {
        final Vec3d eyePos = mc.player.getEyePos();
        final Vec3d rotationVec = Rotation.Builder.build(point, eyePos).getVector();
        final Vec3d maxVec = eyePos.add(rotationVec.x * reach, rotationVec.y * reach, rotationVec.z * reach);

        final Optional<Vec3d> raycast = target.getBoundingBox().raycast(eyePos, maxVec);
        return raycast.map(eyePos::distanceTo).orElse(-1.0);
    }

}
