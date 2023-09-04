package de.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.event.LivingEntityListener;
import de.nekosarekawaii.foxglove.util.rotation.rotationtypes.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected abstract @Nullable RenderLayer getRenderLayer(final T entity, final boolean showBody, final boolean translucent, final boolean showOutline);

    @Shadow
    protected abstract boolean isVisible(final T entity);

    @Shadow
    protected abstract float getAnimationCounter(final T entity, final float tickDelta);

    @Shadow
    protected M model;

    @Unique
    private float rotationPitch;

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;"))
    private @Nullable RenderLayer injectRenderLayer(final LivingEntityRenderer instance, final T entity, final boolean showBody, final boolean translucent, final boolean showOutline) {
        return null;
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;", shift = At.Shift.BEFORE))
    private void injectRender(final T livingEntity, float f, float g, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, int i, final CallbackInfo ci) {
        boolean showBody = this.isVisible(livingEntity),
                translucent = !showBody && !livingEntity.isInvisibleTo(MinecraftClient.getInstance().player),
                showOutline = MinecraftClient.getInstance().hasOutline(livingEntity);
        float red = 1.0f, green = 1.0f, blue = 1.0f, alpha = translucent ? 0.15f : 1.0f;
        final LivingEntityListener.LivingEntityRenderEvent livingEntityRenderEvent = new LivingEntityListener.LivingEntityRenderEvent(livingEntity, f, g, matrixStack, vertexConsumerProvider, i, showBody, translucent, showOutline, red, green, blue, alpha);
        DietrichEvents2.global().postInternal(LivingEntityListener.LivingEntityRenderEvent.ID, livingEntityRenderEvent);
        f = livingEntityRenderEvent.f;
        g = livingEntityRenderEvent.g;
        red = livingEntityRenderEvent.red;
        green = livingEntityRenderEvent.green;
        blue = livingEntityRenderEvent.blue;
        alpha = livingEntityRenderEvent.alpha;
        i = livingEntityRenderEvent.i;
        showBody = livingEntityRenderEvent.showBody;
        translucent = livingEntityRenderEvent.translucent;
        showOutline = livingEntityRenderEvent.showOutline;
        final RenderLayer renderLayer = this.getRenderLayer(livingEntity, showBody, translucent, showOutline);
        if (renderLayer != null) {
            final VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            this.model.render(
                    matrixStack,
                    vertexConsumer,
                    i,
                    LivingEntityRenderer.getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g)),
                    red,
                    green,
                    blue,
                    alpha
            );
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void injectRenderB(final T livingEntity, final float f, final float g, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int i, final CallbackInfo ci) {
        this.rotationPitch = Float.NaN;
        final Rotation rotation = Foxglove.getInstance().getRotationListener().getRotation();
        if (livingEntity != MinecraftClient.getInstance().player || rotation == null) return;
        this.rotationPitch = rotation.getPitch();
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float redirectRotationPitch(final float g, final float f, final float s) {
        if (!Float.isNaN(this.rotationPitch)) return this.rotationPitch;
        return MathHelper.lerp(g, f, s);
    }

}
