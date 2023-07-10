package me.nekosarekawaii.foxglove.util.minecraft.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CollisionUtil {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public boolean isCollidingBlock(final Box boundingBox, final BlockState blockState, final BlockPos blockPos) {
        return boundingBox.intersects(blockState.getCollisionShape(mc.world, blockPos).getBoundingBox());
    }

}