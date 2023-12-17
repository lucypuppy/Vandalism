package de.nekosarekawaii.vandalism.util.minecraft;

import de.florianmichael.rclasses.math.geometry.Trigonometry;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WorldUtil implements MinecraftWrapper {

    public enum Dimension {
        OVERWORLD, NETHER, END
    }

    public static Dimension getDimension() {
        return switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.NETHER;
            case "the_end" -> Dimension.END;
            default -> Dimension.OVERWORLD;
        };
    }

    public static double rayTraceRange(float yaw, float pitch, boolean normalize) {
        Entity entity = mc.player;
        if (entity != null) {
            double d = 4.5; //use the calculation from mc & adjust it by the given range * 1.5
            double e = d;
            Vec3d vec3d = entity.getCameraPosVec(1);
            HitResult crosshairTarget = raycast(yaw, pitch, d);
            ;
            e *= e;
            if (crosshairTarget != null) {
                e = crosshairTarget.getPos().squaredDistanceTo(vec3d);
            }
            Vec3d vec3d2 = getRotationVector(yaw, pitch);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
            Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, entityx -> !entityx.isSpectator() && entityx.canHit(), e);
            if (entityHitResult != null) {
                Vec3d vec3d4 = entityHitResult.getPos();
                double distance = vec3d.squaredDistanceTo(vec3d4);
                if (normalize)
                    distance /= 3;
                return distance;
            }
        }
        return -1;
    }

    private static HitResult raycast(final float yaw, final float pitch, final double maxDistance) {
        Entity entity = mc.player;
        if (entity != null) {
            Vec3d vec3d = entity.getCameraPosVec(1);
            Vec3d vec3d2 = getRotationVector(yaw, pitch);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
            return mc.world
                    .raycast(
                            new RaycastContext(
                                    vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity
                            )
                    );
        } else {
            return null;
        }
    }

    public static Vec3d getRotationVector(float yaw, float pitch) {
        float f = pitch * (float) (Math.PI / 180.0);
        float g = -yaw * (float) (Math.PI / 180.0);
        float h = Trigonometry.TAYLOR.cos(g);
        float i = Trigonometry.TAYLOR.sin(g);
        float j = Trigonometry.TAYLOR.cos(f);
        float k = Trigonometry.TAYLOR.sin(f);
        return new Vec3d((double) (i * j), (double) (-k), (double) (h * j));
    }

    public static boolean rayTraceBlock(final Vec3d targetPosition, final double maxDistance) {
        final Vec3d playerPosition = mc.player.getEyePos();
        final Vec3d lookDirection = targetPosition.subtract(playerPosition).normalize();
        final Vec3d currentPos = playerPosition.add(
                lookDirection.x * maxDistance,
                lookDirection.y * maxDistance,
                lookDirection.z * maxDistance
        );
        final BlockHitResult rayTraceResult = mc.world.raycast(
                new RaycastContext(
                        playerPosition,
                        currentPos,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        mc.player
                )
        );
        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

    public static double getPlayerEyeVectorDistance(final PlayerEntity player, final Vec3d rotationVector) {
        return player.getEyePos().distanceTo(rotationVector);
    }

}
