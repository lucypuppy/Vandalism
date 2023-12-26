package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.WorldListener;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {

    @Shadow
    public abstract Block getBlock();

    @Shadow
    public abstract BlockState asBlockState();

    @Redirect(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    public VoxelShape callBlockListener(final Block block, final BlockState blockState, final BlockView world, final BlockPos pos, final ShapeContext context) {
        final VoxelShape shape = this.getBlock().getCollisionShape(this.asBlockState(), world, pos, context);
        if (pos == null || world == null || MinecraftClient.getInstance() == null || MinecraftClient.getInstance().player == null) {
            return shape;
        }
        final var event = new WorldListener.BlockEvent(block, blockState, world, pos, context, shape);
        Vandalism.getInstance().getEventSystem().postInternal(WorldListener.BlockEvent.ID, event);
        return event.shape;
    }

}
