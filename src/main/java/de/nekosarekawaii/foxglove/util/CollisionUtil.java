package de.nekosarekawaii.foxglove.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class CollisionUtil implements MinecraftWrapper {

    public boolean isCollidingBlock(final Box boundingBox, final BlockState blockState, final BlockPos blockPos) {
        return boundingBox.intersects(blockState.getCollisionShape(world(), blockPos).getBoundingBox());
    }

}