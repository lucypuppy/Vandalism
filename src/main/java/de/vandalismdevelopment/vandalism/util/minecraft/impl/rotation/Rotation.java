package de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.WorldUtil;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class Rotation extends MinecraftUtil {

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

    public void setTargetVector(Vec3d vector) {
        this.targetVector = vector;
    }

    public Vec3d getTargetVeector() {
        return this.targetVector;
    }

    public Vec3d getVector() {
        final float f = pitch * (float) (Math.PI / 180.0);
        final float g = -yaw * (float) (Math.PI / 180.0);
        final float h = MathHelper.cos(g);
        final float i = MathHelper.sin(g);
        final float j = MathHelper.cos(f);
        final float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @Override
    public String toString() {
        return "{" + "yaw=" + this.yaw + ", pitch=" + this.pitch + '}';
    }

    public static class Builder {

        public static Rotation build(final Entity entity, final boolean bestHitVec, final double range, final double precision) {
            if (player() == null) return null;
            final Box box = entity.getBoundingBox();
            final Vec3d eyePos = player().getEyePos(), getEntityVector = bestHitVec ? getNearestPoint(entity, box, player()) : new Vec3d(entity.getX(), entity.getY(), entity.getZ());
            Rotation normalRotations = build(getEntityVector, eyePos);
            //if (WorldUtil.rayTraceBlock(normalRotations.getVector(), range)) {
            //     return normalRotations;
            // }
            normalRotations = null;
            Vec3d currentVector = null;
            for (double x = 0.00D; x < 1.00D; x += precision) {
                for (double y = 0.00D; y < 1.00D; y += precision) {
                    for (double z = 0.00D; z < 1.00D; z += precision) {
                        final Vec3d vector = new Vec3d(box.minX + (box.maxX - box.minX) * x, box.minY + (box.maxY - box.minY) * y, box.minZ + (box.maxZ - box.minZ) * z);
                        if (eyePos.distanceTo(vector) > range) {
                            continue;
                        }
                        final Rotation parsedRotation = build(vector, eyePos);
                        // if (!WorldUtil.rayTraceBlock(parsedRotation.getVector(), range)) {
                        //     continue;
                        // }
                        if (!bestHitVec) {
                            // normalRotations.setTargetVector(vector);
                            parsedRotation.setYaw(build(getEntityVector, eyePos).getYaw());
                            parsedRotation.setPitch(build(getEntityVector.add(0, entity.getStandingEyeHeight(), 0), eyePos).getPitch());

                            parsedRotation.setTargetVector(vector);
                            return parsedRotation;
                        } else if (currentVector == null || eyePos.distanceTo(vector) <= eyePos.distanceTo(currentVector)) {
                            currentVector = vector;
                            normalRotations = parsedRotation;
                            normalRotations.setTargetVector(currentVector);
                        }
                    }
                }
            }
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

    private Pair<Double, Double> generateRandomPointsInCircle3D(float radius) {
        double u = Math.random();
        double v = Math.random();
        double theta = u * 2.0 * Math.PI;
        double phi = Math.acos(2.0 * v - 1.0);
        double r = Math.cbrt(Math.random()) * radius / 2;
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double x = r * sinPhi * cosTheta;
        double z = r * cosPhi;

        return new Pair<>(x, z);
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

    private List<Vec3d> computeHitboxAimPoints(final Entity e, final PlayerEntity player, int aimPoints) {
        final List<Vec3d> points = new ArrayList<>();
        final List<Byte> visibleSides = getVisibleHitBoxSides(e, player);
        final double targetPosY = e.getY() - 0.1;
        final double targetHeight = e.getHeight() + 0.2;
        final double targetWidth = e.getWidth();
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
                    double zOff = (player.getZ() > e.getZ() ? targetWidth / 2 : -targetWidth / 2);
                    points.add(new Vec3d(e.getX() - targetWidth / 2 + x, targetPosY + y, e.getZ() + zOff));
                }
            }
        }

        if (visibleSides.contains((byte) 2)) { // y
            for (double y = 0; y <= targetWidth; y += horDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double yOff = (player.getEyeY() < targetPosY ? 0 : targetHeight);
                    points.add(new Vec3d(e.getX() - targetWidth / 2 + x, targetPosY + yOff, e.getZ() - targetWidth / 2 + y));
                }
            }
        }

        if (visibleSides.contains((byte) 1)) { // z
            for (double y = 0; y <= targetHeight; y += vertDist) {
                for (double x = 0; x <= targetWidth; x += horDist) {
                    double xOff = (player.getX() > e.getX() ? targetWidth / 2 : -targetWidth / 2);
                    points.add(new Vec3d(e.getX() + xOff, targetPosY + y, e.getZ() - targetWidth / 2 + x));
                }
            }
        }

        return points;
    }

}
