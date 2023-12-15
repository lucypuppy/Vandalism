package de.nekosarekawaii.vandalism.injection.mixins.event;

import com.llamalad7.mixinextras.sugar.Local;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.base.event.render.LivingEntityRenderBottomLayerListener;
import de.nekosarekawaii.vandalism.base.event.render.LivingEntityRenderPostListener;
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

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void callLivingEntityRenderBottomLayerListener(final EntityModel<T> instance, final MatrixStack matrices, final VertexConsumer vertices, final int light, final int overlay, final float red, final float green, final float blue, final float alpha, @Local T livingEntity) {
        final var event = new LivingEntityRenderBottomLayerListener.LivingEntityRenderBottomLayerEvent(livingEntity, matrices, vertices, light, overlay, red, green, blue, alpha);
        DietrichEvents2.global().postInternal(LivingEntityRenderBottomLayerListener.LivingEntityRenderBottomLayerEvent.ID, event);
        instance.render(matrices, vertices, event.light, event.overlay, event.red, event.green, event.blue, event.alpha);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE))
    private void callLivingEntityRenderPostListener(final T livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(LivingEntityRenderPostListener.LivingEntityRenderPostEvent.ID, new LivingEntityRenderPostListener.LivingEntityRenderPostEvent(livingEntity, yaw, tickDelta, matrixStack, light));
    }

}
