package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.BlockListener;
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
    public void getCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> callback) {
        if (pos == null)
            return;

        final BlockListener.BlockEvent shapeEvent = new BlockListener.BlockEvent(((AbstractBlock.AbstractBlockState)(Object) this).asBlockState(), pos, callback.getReturnValue());
        DietrichEvents2.global().postInternal(BlockListener.BlockEvent.ID, shapeEvent);
        if (shapeEvent.shouldUpdate) callback.setReturnValue(shapeEvent.shape);
    }

}