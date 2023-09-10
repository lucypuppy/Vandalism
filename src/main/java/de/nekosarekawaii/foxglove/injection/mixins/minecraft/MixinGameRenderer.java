package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Shadow
    @Final
    MinecraftClient client;

    @Inject(method = "renderNausea", at = @At(value = "HEAD"), cancellable = true)
    public void renderNausea(final DrawContext context, final float distortionStrength, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().nauseaOverlay.getValue()) ci.cancel();
    }

    @Inject(method = "tiltViewWhenHurt", at = @At(value = "HEAD"), cancellable = true)
    public void tiltViewWhenHurt(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().hurtCam.getValue()) ci.cancel();
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void injectBobView(final MatrixStack matrixStack, final float f, final CallbackInfo callbackInfo) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().customBobView.getValue()) {
            return;
        }
        if (!(this.client.getCameraEntity() instanceof PlayerEntity playerEntity)) {
            return;
        }
        final float additionalBobbing = Foxglove.getInstance().getConfigManager().getMainConfig().customBobViewValue.getValue();
        if (additionalBobbing <= 0f) {
            callbackInfo.cancel();
            return;
        }
        final float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed,
                h = -(playerEntity.horizontalSpeed + g * f),
                i = MathHelper.lerp(f, playerEntity.prevStrideDistance, playerEntity.strideDistance);
        matrixStack.translate((MathHelper.sin(h * MathHelper.PI) * i * 0.5F), -Math.abs(MathHelper.cos(h * MathHelper.PI) * i), 0.0D);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(h * MathHelper.PI) * i * (3.0F + additionalBobbing)));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(h * MathHelper.PI - (0.2F + additionalBobbing)) * i) * 5.0F));
        callbackInfo.cancel();
    }

}
