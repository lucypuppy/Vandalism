package de.vandalismdevelopment.vandalism.util.minecraft.impl;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;

public class WorldUtil extends MinecraftUtil {

    public enum Dimension {
        OVERWORLD, NETHER, END
    }

    public static Dimension getDimension() {
        if (world() == null) return Dimension.OVERWORLD;
        return switch (world().getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.NETHER;
            case "the_end" -> Dimension.END;
            default -> Dimension.OVERWORLD;
        };
    }

    public static boolean rayTraceBlock(final Vec3d targetPosition, final double maxDistance) {
        if (player() == null || world() == null) {
            return false;
        }

        final Vec3d playerPosition = player().getEyePos();
        final Vec3d lookDirection = targetPosition.subtract(playerPosition).normalize();
        final Vec3d currentPos = playerPosition.add(
                lookDirection.x * maxDistance,
                lookDirection.y * maxDistance,
                lookDirection.z * maxDistance
        );
        final BlockHitResult rayTraceResult = world().raycast(
                new RaycastContext(
                        playerPosition,
                        currentPos,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player()
                )
        );
        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

    public static boolean rayTraceBlock(final Vec3d end) {
        if (player() == null || world() == null) {
            return false;
        }

        final BlockHitResult rayTraceResult = rayBlock(player().getEyePos(), end);
        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

    private static BlockHitResult rayBlock(final Vec3d start, final Vec3d end) {
        double d = MathHelper.lerp(-1.0E-7, end.x, start.x);
        double e = MathHelper.lerp(-1.0E-7, end.y, start.y);
        double f = MathHelper.lerp(-1.0E-7, end.z, start.z);
        double g = MathHelper.lerp(-1.0E-7, start.x, end.x);
        double h = MathHelper.lerp(-1.0E-7, start.y, end.y);
        double i = MathHelper.lerp(-1.0E-7, start.z, end.z);
        int j = MathHelper.floor(g);
        int k = MathHelper.floor(h);
        int l = MathHelper.floor(i);
        final BlockPos.Mutable blockPos = new BlockPos.Mutable(j, k, l);

        BlockState blockState = world().getBlockState(blockPos);
        VoxelShape blockShape = blockState.getRaycastShape(world(), blockPos);

        final BlockHitResult rayTraceResult = world().raycastBlock(start, end, blockPos, blockShape, blockState);
        if (rayTraceResult != null) return rayTraceResult;

        double m = d - g;
        double n = e - h;
        double o = f - i;
        int p = MathHelper.sign(m);
        int q = MathHelper.sign(n);
        int r = MathHelper.sign(o);
        double s = p == 0 ? Double.MAX_VALUE : (double) p / m;
        double t = q == 0 ? Double.MAX_VALUE : (double) q / n;
        double u = r == 0 ? Double.MAX_VALUE : (double) r / o;
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
            blockState = world().getBlockState(blockPos);
            blockShape = blockState.getRaycastShape(world(), blockPos);
            result = world().raycastBlock(start, end, blockPos, blockShape, blockState);
        } while (result == null);

        return result;
    }

    public static double getPlayerEyeVectorDistance(final PlayerEntity player, final Vec3d rotationVector) {
        return player.getEyePos().distanceTo(rotationVector);
    }

}
