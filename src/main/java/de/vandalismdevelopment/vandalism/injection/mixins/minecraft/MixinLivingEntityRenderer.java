package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.LivingEntityListener;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.TrueSightModule;
import de.vandalismdevelopment.vandalism.util.rotation.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected MixinLivingEntityRenderer(final EntityRendererFactory.Context ignored) {
        super(ignored);
    }

    @Unique
    private T livingEntity;

    @Unique
    private float rotationPitch;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"))
    private void injectRender(final T livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        this.livingEntity = livingEntity;
        this.rotationPitch = Float.NaN;
        final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (livingEntity != MinecraftClient.getInstance().player || rotation == null) return;
        this.rotationPitch = rotation.getPitch();
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float redirectRotationPitch(final float tickDelta, final float prevPitch, final float pitch) {
        if (!Float.isNaN(this.rotationPitch)) return this.rotationPitch;
        return MathHelper.lerp(tickDelta, prevPitch, pitch);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void redirectRenderBottomLayer(final EntityModel<T> instance, final MatrixStack matrices, final VertexConsumer vertices, final int light, final int overlay, final float red, final float green, final float blue, final float alpha) {
        final LivingEntityListener.LivingEntityRenderBottomLayerEvent livingEntityRenderBottomLayerEvent = new LivingEntityListener.LivingEntityRenderBottomLayerEvent(this.livingEntity, matrices, vertices, light, overlay, red, green, blue, alpha);
        DietrichEvents2.global().postInternal(LivingEntityListener.LivingEntityRenderBottomLayerEvent.ID, livingEntityRenderBottomLayerEvent);
        instance.render(matrices, vertices, livingEntityRenderBottomLayerEvent.light, livingEntityRenderBottomLayerEvent.overlay, livingEntityRenderBottomLayerEvent.red, livingEntityRenderBottomLayerEvent.green, livingEntityRenderBottomLayerEvent.blue, livingEntityRenderBottomLayerEvent.alpha);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE))
    private void injectRenderForRenderPostEvent(final T livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        final LivingEntityListener.LivingEntityRenderPostEvent livingEntityRenderPostEvent = new LivingEntityListener.LivingEntityRenderPostEvent(livingEntity, yaw, tickDelta, matrixStack, light);
        DietrichEvents2.global().postInternal(LivingEntityListener.LivingEntityRenderPostEvent.ID, livingEntityRenderPostEvent);
    }

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private void injectGetRenderLayer(final T entity, final boolean showBody, final boolean translucent, final boolean showOutline, final CallbackInfoReturnable<RenderLayer> cir) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleRegistry().getTrueSightModule();
        if (trueSightModule.isEnabled() && !showBody && !translucent && !showOutline) {
            cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(this.getTexture(entity)));
        }
    }

}
