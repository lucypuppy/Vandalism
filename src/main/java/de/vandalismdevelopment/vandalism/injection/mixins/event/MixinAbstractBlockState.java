package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.network.WorldListener;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    public void vandalism$callBlockEvent(final BlockView world, final BlockPos pos, final ShapeContext context, final CallbackInfoReturnable<VoxelShape> callback) {
        if (pos == null) {
            return;
        }
        final WorldListener.BlockEvent blockEvent = new WorldListener.BlockEvent(((AbstractBlock.AbstractBlockState) (Object) this).asBlockState(), pos, callback.getReturnValue());
        DietrichEvents2.global().postInternal(WorldListener.BlockEvent.ID, blockEvent);
        if (blockEvent.shouldUpdate) {
            callback.setReturnValue(blockEvent.shape);
        }
    }

}