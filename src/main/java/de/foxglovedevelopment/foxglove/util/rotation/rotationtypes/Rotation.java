package de.foxglovedevelopment.foxglove.util.rotation.rotationtypes;

import de.foxglovedevelopment.foxglove.util.RaytraceUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Rotation {

    private float yaw, pitch;

    public Rotation(float yaw, float pitch) {
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
        return "Rotation{" + "yaw=" + yaw + ", pitch=" + pitch + '}';
    }

    public static class Builder {

        public static Rotation build(final Entity entity, final boolean bestHitVec, final double range, final double precision) {
            final PlayerEntity player = MinecraftClient.getInstance().player;
            final Vec3d eyePos = player.getEyePos();
            final Box box = entity.getBoundingBox();
            final Vec3d getEntityVector = bestHitVec ? getNearestPoint(entity, box, player) : new Vec3d(entity.getX(), entity.getY(), entity.getZ());

            Rotation normalRotations = build(getEntityVector, eyePos);
            if (RaytraceUtil.rayTraceBlock(normalRotations.getVector(), range))
                return normalRotations;

            normalRotations = null;
            Vec3d currentVector = null;
            for (double x = 0.00D; x < 1.00D; x += precision) {
                for (double y = 0.00D; y < 1.00D; y += precision) {
                    for (double z = 0.00D; z < 1.00D; z += precision) {
                        final Vec3d vector = new Vec3d(
                                box.minX + (box.maxX - box.minX) * x,
                                box.minY + (box.maxY - box.minY) * y,
                                box.minZ + (box.maxZ - box.minZ) * z);

                        if (eyePos.distanceTo(vector) > range)
                            continue;

                        final Rotation parsedRotation = build(vector, eyePos);

                        if (!RaytraceUtil.rayTraceBlock(parsedRotation.getVector(), range))
                            continue;

                        if (!bestHitVec) {
                            return parsedRotation;
                        } else if (currentVector == null || eyePos.distanceTo(vector) <= eyePos.distanceTo(currentVector)) {
                            currentVector = vector;
                            normalRotations = parsedRotation;
                        }
                    }
                }
            }

            return normalRotations;
        }

        public static Rotation build(final Vec3d to, final Vec3d eyePos) {
            final Vec3d diff = to.subtract(eyePos);
            final double hypot = Math.hypot(diff.getX(), diff.getZ());
            final float
                    yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * (180.0F / Math.PI)) - 90.0F,
                    pitch = (float) (-MathHelper.atan2(diff.getY(), hypot) * (180.0F / Math.PI));

            return new Rotation(yaw, pitch);
        }

        private static Vec3d getNearestPoint(final Entity entity, final Box box, final PlayerEntity player) {
            final double nearestX = MathHelper.clamp(entity.getX(), box.minX, box.maxX);
            final double nearestZ = MathHelper.clamp(entity.getZ(), box.minZ, box.maxZ);

            //Todo find a better way to calculate this
            final double entityY = entity.getY();
            final double playerY = player.getY() + player.getEyeHeight(player.getPose());
            final double boxHeight = (box.maxY - box.minY) * 0.9;
            final double nearestY = entityY + MathHelper.clamp(playerY - entityY, 0, boxHeight);

            return new Vec3d(nearestX, nearestY, nearestZ);
        }

    }

}
