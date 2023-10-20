package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.ESPModule;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Inject(method = "hasBlindnessOrDarkness", at = @At("HEAD"), cancellable = true)
    private void injectHasBlindnessOrDarknessEffect(final Camera camera, final CallbackInfoReturnable<Boolean> ci) {
        if (!Vandalism.getInstance().getConfigManager().getMainConfig().visualCategory.blindnessEffect.getValue()) {
            ci.setReturnValue(false);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"))
    private void redirectSetOutlineColor(final OutlineVertexConsumerProvider instance, final int red, final int green, final int blue, final int alpha) {
        final ESPModule espModule = Vandalism.getInstance().getModuleRegistry().getEspModule();
        if (espModule.isEnabled()) {
            final Color color = espModule.outlineColor.getValue();
            instance.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            return;
        }
        instance.setColor(red, green, blue, alpha);
    }


}