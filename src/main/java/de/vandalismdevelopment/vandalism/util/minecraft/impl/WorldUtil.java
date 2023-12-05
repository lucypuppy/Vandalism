package de.vandalismdevelopment.vandalism.util.minecraft.impl;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
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
        final Vec3d currentPos = playerPosition.add(lookDirection.x * maxDistance, lookDirection.y * maxDistance, lookDirection.z * maxDistance);
        final BlockHitResult rayTraceResult = world().raycast(new RaycastContext(playerPosition, currentPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player()));
        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

    public static double getPlayerEyeVectorDistance(PlayerEntity player, Vec3d rotationVector){
        final Vec3d eyes = player.getEyePos();
        return eyes.distanceTo(rotationVector);
    }

}
