package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.VisualThrottleModule;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureBlockBlockEntityRenderer.class)
public abstract class MixinStructureBlockBlockEntityRenderer {

    @Shadow
    protected abstract void renderInvisibleBlocks(StructureBlockBlockEntity entity, VertexConsumer vertices, BlockPos pos, MatrixStack matrices);

    @Redirect(method = "render(Lnet/minecraft/block/entity/StructureBlockBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDDDDFFFFFFF)V"))
    private void hookVisualThrottle_BoundingBoxes(final MatrixStack matrices, final VertexConsumer vertexConsumer, final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final float red, final float green, final float blue, final float alpha, final float xAxisRed, final float yAxisGreen, final float zAxisBlue) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockStructureBlockBoundingBoxes.getValue()) {
            return;
        }
        WorldRenderer.drawBox(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, xAxisRed, yAxisGreen, zAxisBlue);
    }

    @Redirect(method = "render(Lnet/minecraft/block/entity/StructureBlockBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/StructureBlockBlockEntityRenderer;renderInvisibleBlocks(Lnet/minecraft/block/entity/StructureBlockBlockEntity;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void hookVisualThrottle_AirBoxes(final StructureBlockBlockEntityRenderer structureBlockBlockEntityRenderer, final StructureBlockBlockEntity structureBlockBlockEntity, final VertexConsumer vertexConsumer, final BlockPos blockPos, final MatrixStack matrixStack) {
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockStructureBlockAirBoxes.getValue()) {
            return;
        }
        this.renderInvisibleBlocks(structureBlockBlockEntity, vertexConsumer, blockPos, matrixStack);
    }

}
