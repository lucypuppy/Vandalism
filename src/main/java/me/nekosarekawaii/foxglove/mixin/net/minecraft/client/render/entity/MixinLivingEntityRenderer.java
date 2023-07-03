package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.render.entity;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.TrueSightModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected abstract @Nullable RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline);

    @Shadow
    protected abstract boolean isVisible(T entity);

    @Shadow
    protected abstract float getAnimationCounter(T entity, float tickDelta);

    @Shadow
    protected M model;

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;"))
    private @Nullable RenderLayer injectRenderLayer(final LivingEntityRenderer instance, final T entity, final boolean showBody, final boolean translucent, final boolean showOutline) {
        final RenderLayer renderLayer = this.getRenderLayer(entity, showBody, translucent, showOutline);
        final TrueSightModule trueSightModule = Foxglove.getInstance().getModuleRegistry().getTrueSightModule();
        if (trueSightModule.isEnabled() && trueSightModule.entities.getValue()) {
            return null;
        }
        return renderLayer;
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;", shift = At.Shift.BEFORE))
    private void injectRender(final T livingEntity, final float f, final float g, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int i, final CallbackInfo ci) {
        final TrueSightModule trueSightModule = Foxglove.getInstance().getModuleRegistry().getTrueSightModule();
        if (trueSightModule.isEnabled() && trueSightModule.entities.getValue()) {
            final boolean showBody = this.isVisible(livingEntity), showOutline = MinecraftClient.getInstance().hasOutline(livingEntity);
            //final boolean translucent = !showBody && !livingEntity.isInvisibleTo(MinecraftClient.getInstance().player)
            final boolean translucent = !showBody;
            final VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.getRenderLayer(livingEntity, showBody, translucent, showOutline));
            //TODO: Make something like a event or so that we can change the rgba of every living entity.
            this.model.render(
                    matrixStack,
                    vertexConsumer,
                    i,
                    LivingEntityRenderer.getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g)),
                    1.0f,
                    1.0f,
                    1.0f,
                    translucent ? 0.5f : 1.0f
            );
        }
    }


}
