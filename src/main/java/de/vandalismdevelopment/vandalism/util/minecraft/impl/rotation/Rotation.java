package de.vandalismdevelopment.vandalism.util.minecraft.impl.rotation;

import de.vandalismdevelopment.vandalism.util.minecraft.impl.ChatUtil;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.WorldUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class Rotation {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private float yaw, pitch;

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
            if (mc().player == null) return null;
            final Box box = entity.getBoundingBox();
            final Vec3d eyePos = mc().player.getEyePos(), getEntityVector = bestHitVec ? getNearestPoint(entity, box, mc().player) : new Vec3d(entity.getX(), entity.getY(), entity.getZ());
            Rotation normalRotations = build(getEntityVector, eyePos);
            if (WorldUtil.rayTraceBlock(normalRotations.getVector(), range)) {
                return normalRotations;
            }
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
                        if (!WorldUtil.rayTraceBlock(parsedRotation.getVector(), range)) {
                            continue;
                        }
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

    private static ArrayList<Character> getVisibleHitBoxSides(Entity e, final PlayerEntity player) {
        final ArrayList<Character> sides = new ArrayList<>();
        //TODO: check if anything has changed in 1.20.2 regarding hitbox position offsetting
        final float width = (e.getWidth() + 0.2f) / 2f;
        final float height = e.getHeight() + 0.2f;
        final double eposY = e.getY() - 0.1;

        if (player.getZ() < e.getZ() - width || player.getZ() > e.getZ() + width) {
            sides.add('x');
        }
        if (player.getX() < e.getX() - width || player.getX() > e.getX() + width) {
            sides.add('z');
        }
        if (player.getY() + player.getEyeHeight(player.getPose()) < eposY || player.getY() + player.getEyeHeight(player.getPose()) > eposY + height) {
            sides.add('y');
        }

        return sides;
    }

}
