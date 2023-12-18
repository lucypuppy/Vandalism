package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.KillAuraModule;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.minecraft.MathUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements MinecraftWrapper {

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double hookKillAuraCombatRange(final double constant) {
        final KillAuraModule killauraModule = Vandalism.getInstance().getModuleManager().getKillauraModule();
        return killauraModule.isActive() ? MathUtil.getFixedMinecraftReach(killauraModule.range.getValue()) : constant;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V", shift = At.Shift.AFTER))
    private void hookRotation(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(RotationListener.RotationEvent.ID, new RotationListener.RotationEvent());
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void customBobView(final MatrixStack matrixStack, final float f, final CallbackInfo callbackInfo) {
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
