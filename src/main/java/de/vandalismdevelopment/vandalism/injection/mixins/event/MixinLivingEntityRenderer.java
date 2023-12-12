package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.render.CameraClipRaytraceListener;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected MixinLivingEntityRenderer(final EntityRendererFactory.Context ignored) {
        super(ignored);
    }

    @Unique
    private T vandalism_livingEntity;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"))
    private void vandalism$initSetLivingEntity(final T livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        this.vandalism_livingEntity = livingEntity;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void vandalism$callLivingEntityRenderBottomLayerEvent(final EntityModel<T> instance, final MatrixStack matrices, final VertexConsumer vertices, final int light, final int overlay, final float red, final float green, final float blue, final float alpha) {
        final CameraClipRaytraceListener.LivingEntityRenderBottomLayerEvent livingEntityRenderBottomLayerEvent = new CameraClipRaytraceListener.LivingEntityRenderBottomLayerEvent(this.vandalism_livingEntity, matrices, vertices, light, overlay, red, green, blue, alpha);
        DietrichEvents2.global().postInternal(CameraClipRaytraceListener.LivingEntityRenderBottomLayerEvent.ID, livingEntityRenderBottomLayerEvent);
        instance.render(matrices, vertices, livingEntityRenderBottomLayerEvent.light, livingEntityRenderBottomLayerEvent.overlay, livingEntityRenderBottomLayerEvent.red, livingEntityRenderBottomLayerEvent.green, livingEntityRenderBottomLayerEvent.blue, livingEntityRenderBottomLayerEvent.alpha);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE))
    private void vandalism$callLivingEntityRenderPostEvent(final T livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(CameraClipRaytraceListener.LivingEntityRenderPostEvent.ID, new CameraClipRaytraceListener.LivingEntityRenderPostEvent(livingEntity, yaw, tickDelta, matrixStack, light));
    }

}