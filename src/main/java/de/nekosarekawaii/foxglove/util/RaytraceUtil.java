package de.nekosarekawaii.foxglove.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RaytraceUtil {

    public static boolean rayTraceBlock(final Vec3d targetPosition, final double maxDistance) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final ClientPlayerEntity player = mc.player;
        final World world = mc.world;

        if (player == null || world == null)
            return false;

        final Vec3d playerPosition = player.getEyePos();
        final Vec3d lookDirection = targetPosition.subtract(playerPosition).normalize();

        final Vec3d currentPos = playerPosition.add(lookDirection.x * maxDistance, lookDirection.y * maxDistance, lookDirection.z * maxDistance);
        final BlockHitResult rayTraceResult = world.raycast(
                new RaycastContext(playerPosition, currentPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player)
        );

        return rayTraceResult == null || rayTraceResult.getType() != HitResult.Type.BLOCK;
    }

}
