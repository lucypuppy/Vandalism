package de.vandalismdevelopment.vandalism.injection.mixins.feature.config;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements MinecraftWrapper {

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void vandalism$customBobView(final MatrixStack matrixStack, final float f, final CallbackInfo callbackInfo) {
        if (!Vandalism.getInstance().getClientSettings().getVisualSettings().customBobView.getValue()) {
            return;
        }
        if (!(this.mc.getCameraEntity() instanceof PlayerEntity playerEntity)) {
            return;
        }
        final float additionalBobbing = Vandalism.getInstance().getClientSettings().getVisualSettings().customBobViewValue.getValue();
        if (additionalBobbing <= 0f) {
            callbackInfo.cancel();
            return;
        }
        final float g = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed, h = -(playerEntity.horizontalSpeed + g * f), i = MathHelper.lerp(f, playerEntity.prevStrideDistance, playerEntity.strideDistance);
        matrixStack.translate((MathHelper.sin(h * MathHelper.PI) * i * 0.5F), -Math.abs(MathHelper.cos(h * MathHelper.PI) * i), 0.0D);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(h * MathHelper.PI) * i * (3.0F + additionalBobbing)));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(h * MathHelper.PI - (0.2F + additionalBobbing)) * i) * 5.0F));
        callbackInfo.cancel();
    }

}
