package de.foxglovedevelopment.foxglove.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CollisionUtil {

    public static boolean isCollidingBlock(final Box boundingBox, final BlockState blockState, final BlockPos blockPos) {
        return boundingBox.intersects(blockState.getCollisionShape(MinecraftClient.getInstance().world, blockPos).getBoundingBox());
    }

}