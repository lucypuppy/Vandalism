package de.nekosarekawaii.vandalism.integration.rotation;

import de.florianmichael.rclasses.functional.tuple.Pair;
import de.florianmichael.rclasses.functional.tuple.immutable.ImmutablePair;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.minecraft.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
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

        public static Rotation build(final Entity entity, final double range, final int aimPoints) {
            if (mc.player == null) return null;
            Rotation normalRotations = null;
            final List<Vec3d> possibleHitBoxPoints = computeHitboxAimPoints(entity, mc.player, aimPoints);
            final List<Vec3d> hitAblePoints = new ArrayList<>();

            double bestDistance = 99;
            Vec3d bestHitBoxVector = null;
            for (Vec3d hitboxVector : possibleHitBoxPoints) {
                final float[] simulatedRotation = getRotationToPoint(hitboxVector, mc.player);
                final Pair<HitResult, Double> raytrace = WorldUtil.rayTrace(
                        new Rotation(simulatedRotation[0], simulatedRotation[1]),
                        mc.player.getCameraPosVec(1.0f),
                        range
                );

                final double hitBoxDistance = raytrace != null ? raytrace.getSecond() : -1.0;

                // if (hitBoxDistance >= 3.0)
                //     ChatUtil.infoChatMessage("" + hitBoxDistance);

                if (hitBoxDistance > 0 && hitBoxDistance <= range) {
                    if (bestDistance > hitBoxDistance) {
                        bestDistance = hitBoxDistance;
                        bestHitBoxVector = hitboxVector;
                    }

                    hitAblePoints.add(hitboxVector);
                }
            }

            possibleHitBoxPoints.clear();

            if (bestHitBoxVector != null) {
                final float[] rotations = getRotationToPoint(bestHitBoxVector, mc.player);
                normalRotations = new Rotation(rotations[0], rotations[1]);
            }

            hitAblePoints.clear();

            //TODO: add more logic
            return normalRotations;
        }

        public static Rotation build(final Vec3d to, final Vec3d eyePos) {
            final Vec3d diff = to.subtract(eyePos);
            final double hypot = Math.hypot(diff.getX(), diff.getZ());
            final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0F / Math.PI)) - 90.0F;
            final float pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0F / Math.PI));
            return new Rotation(yaw, pitch);
        }

        private static Vec3d getNearestPoint(final Entity entity, final Box box, final PlayerEntity player) {
            final double nearestX = MathHelper.clamp(entity.getX(), box.minX, box.maxX);
            final double nearestZ = MathHelper.clamp(entity.getZ(), box.minZ, box.maxZ);
            //TODO: Find a better way to calculate this!
            final double entityY = entity.getY();
            final double playerY = player.getY() + player.getEyeHeight(player.getPose());
            final double boxHeight = (box.maxY - box.minY) * 0.9;
            final double nearestY = entityY + MathHelper.clamp(playerY - entityY, 0, boxHeight);
            return new Vec3d(nearestX, nearestY, nearestZ);
        }

    }

    private Pair<Double, Double> generateRandomPointsInCircle3D(final float radius) {
        final double u = Math.random();
        final double v = Math.random();
        final double theta = u * 2.0 * Math.PI;
        final double phi = Math.acos(2.0 * v - 1.0);
        final double r = Math.cbrt(Math.random()) * radius / 2;
        final double cosTheta = Math.cos(theta);
        final double sinPhi = Math.sin(phi);
        final double cosPhi = Math.cos(phi);
        return new ImmutablePair<>(r * sinPhi * cosTheta, r * cosPhi);
    }

    private static List<Byte> getVisibleHitBoxSides(final Entity entity, final PlayerEntity player) {
        final List<Byte> sides = new ArrayList<>();
        //TODO: check if anything has changed in 1.20.2 regarding hitbox position offsetting
        final float width = (entity.getWidth() + 0.2f) / 2f;
        final float height = entity.getHeight() + 0.2f;
        final double eyePosY = entity.getY() - 0.1;
        if (player.getZ() < entity.getZ() - width || player.getZ() > entity.getZ() + width) {
            sides.add((byte) 0); //x
        }
        if (player.getX() < entity.getX() - width || player.getX() > entity.getX() + width) {
            sides.add((byte) 1); //Z
        }
        if (player.getY() + player.getEyeHeight(player.getPose()) < eyePosY || player.getY() + player.getEyeHeight(player.getPose()) > eyePosY + height) {
            sides.add((byte) 2); //y
        }
        return sides;
    }

    private static List<Vec3d> computeHitboxAimPoints(final Entity entity, final PlayerEntity player, final int aimPoints) {
        final List<Vec3d> points = new ArrayList<>();
        final List<Byte> visibleSides = getVisibleHitBoxSides(entity, player);
        final double targetPosY = entity.getY() - 0.1;
        final double targetHeight = entity.getHeight() + 0.2;
        final double targetWidth = entity.getWidth();
        /*
         * hitbox formula:
         * visibleSides * width * height
         * (points * points)
         */
        final double horDist = targetWidth / aimPoints;
        final double vertDist = targetHeight / aimPoints;
        if (visibleSides.contains((byte) 0)) { // x
            for (double y = 0; y <= targetHeight; y += vertDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double zOff = (player.getZ() > entity.getZ() ? targetWidth / 2 : -targetWidth / 2);
                    points.add(new Vec3d(entity.getX() - targetWidth / 2 + x, targetPosY + y, entity.getZ() + zOff));
                }
            }
        }

        if (visibleSides.contains((byte) 2)) { // y
            for (double y = 0; y <= targetWidth; y += horDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double yOff = (player.getEyeY() < targetPosY ? 0 : targetHeight);
                    points.add(new Vec3d(entity.getX() - targetWidth / 2 + x, targetPosY + yOff, entity.getZ() - targetWidth / 2 + y));
                }
            }
        }

        if (visibleSides.contains((byte) 1)) { // z
            for (double y = 0; y <= targetHeight; y += vertDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double xOff = (player.getX() > entity.getX() ? targetWidth / 2 : -targetWidth / 2);
                    points.add(new Vec3d(entity.getX() + xOff, targetPosY + y, entity.getZ() - targetWidth / 2 + x));
                }
            }
        }

        return points;
    }

    private static float[] getRotationToPoint(final Vec3d p, final PlayerEntity player) {
        final double deltaX = p.getX() - player.getX();
        final double deltaZ = p.getZ() - player.getZ();
        final double deltaY = p.getY() - player.getEyeY();
        final double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180D / Math.PI) - 90F;
        final float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));
        return new float[]{yaw, pitch};
    }

}
