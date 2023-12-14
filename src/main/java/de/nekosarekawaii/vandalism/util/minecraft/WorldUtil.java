package de.nekosarekawaii.vandalism.util.minecraft;

import de.florianmichael.rclasses.math.geometry.Trigonometry;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
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

    public static double rayTraceRange(float yaw, float pitch) {
        Entity entity = mc.player;
        if (entity != null) {
            double d = 4.5; //use the calculation from mc & adjust it by the given range * 1.5
            Vec3d vec3d = entity.getCameraPosVec(1);
            Vec3d vec3d2 = Vec3d.fromPolar(pitch, yaw);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
            Box box = entity.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
            EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, entityx -> !entityx.isSpectator() && entityx.canHit(), 16);
            if (entityHitResult != null) {
                Vec3d vec3d4 = entityHitResult.getPos();
                return vec3d.distanceTo(vec3d4);
            }
        }
        return -1;
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

    public static boolean rayTraceBlock(final Vec3d end) {
        final BlockHitResult rayTraceResult = rayBlock(mc.player.getEyePos(), end);
        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

    private static BlockHitResult rayBlock(final Vec3d start, final Vec3d end) {
        final double d = MathHelper.lerp(-1.0E-7, end.x, start.x);
        final double e = MathHelper.lerp(-1.0E-7, end.y, start.y);
        final double f = MathHelper.lerp(-1.0E-7, end.z, start.z);
        final double g = MathHelper.lerp(-1.0E-7, start.x, end.x);
        final double h = MathHelper.lerp(-1.0E-7, start.y, end.y);
        final double i = MathHelper.lerp(-1.0E-7, start.z, end.z);
        int j = MathHelper.floor(g);
        int k = MathHelper.floor(h);
        int l = MathHelper.floor(i);
        final BlockPos.Mutable blockPos = new BlockPos.Mutable(j, k, l);

        BlockState blockState = mc.world.getBlockState(blockPos);
        VoxelShape blockShape = blockState.getRaycastShape(mc.world, blockPos);

        final BlockHitResult rayTraceResult = mc.world.raycastBlock(start, end, blockPos, blockShape, blockState);
        if (rayTraceResult != null) return rayTraceResult;

        final double m = d - g;
        final double n = e - h;
        final double o = f - i;
        final int p = MathHelper.sign(m);
        final int q = MathHelper.sign(n);
        final int r = MathHelper.sign(o);
        final double s = p == 0 ? Double.MAX_VALUE : (double) p / m;
        final double t = q == 0 ? Double.MAX_VALUE : (double) q / n;
        final double u = r == 0 ? Double.MAX_VALUE : (double) r / o;
        double v = s * (p > 0 ? 1.0 - MathHelper.fractionalPart(g) : MathHelper.fractionalPart(g));
        double w = t * (q > 0 ? 1.0 - MathHelper.fractionalPart(h) : MathHelper.fractionalPart(h));
        double x = u * (r > 0 ? 1.0 - MathHelper.fractionalPart(i) : MathHelper.fractionalPart(i));

        BlockHitResult result;
        do {
            if (!(v <= 1.0) && !(w <= 1.0) && !(x <= 1.0)) {
                return null;
            }

            if (v < w) {
                if (v < x) {
                    j += p;
                    v += s;
                } else {
                    l += r;
                    x += u;
                }
            } else if (w < x) {
                k += q;
                w += t;
            } else {
                l += r;
                x += u;
            }

            blockPos.set(j, k, l);
            blockState = mc.world.getBlockState(blockPos);
            blockShape = blockState.getRaycastShape(mc.world, blockPos);
            result = mc.world.raycastBlock(start, end, blockPos, blockShape, blockState);
        } while (result == null);

        return result;
    }

    public static double getPlayerEyeVectorDistance(final PlayerEntity player, final Vec3d rotationVector) {
        return player.getEyePos().distanceTo(rotationVector);
    }

}
