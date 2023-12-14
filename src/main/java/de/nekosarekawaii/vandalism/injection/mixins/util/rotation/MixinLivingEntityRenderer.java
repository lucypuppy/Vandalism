package de.nekosarekawaii.vandalism.injection.mixins.util.rotation;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.rotation.Rotation;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
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

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>, MinecraftWrapper {

    @Unique
    private float vandalism$rotationPitch;

    protected MixinLivingEntityRenderer(final EntityRendererFactory.Context ignored) {
        super(ignored);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"))
    private void vandalism$initRenderedModRotationPitch(final T livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int light, final CallbackInfo ci) {
        this.vandalism$rotationPitch = Float.NaN;
        final Rotation rotation = Vandalism.getInstance().getRotationListener().getRotation();
        if (livingEntity != this.mc.player || rotation == null) return;
        this.vandalism$rotationPitch = rotation.getPitch();
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float vandalism$setRenderedModRotationPitch(final float tickDelta, final float prevPitch, final float pitch) {
        if (!Float.isNaN(this.vandalism$rotationPitch)) return this.vandalism$rotationPitch;
        return MathHelper.lerp(tickDelta, prevPitch, pitch);
    }

}
