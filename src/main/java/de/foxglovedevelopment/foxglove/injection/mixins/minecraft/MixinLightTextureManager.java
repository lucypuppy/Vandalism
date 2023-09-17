package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import de.foxglovedevelopment.foxglove.Foxglove;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightTextureManager {

    @Inject(method = "getDarknessFactor", at = @At("HEAD"), cancellable = true)
    private void injectGetDarknessFactor(final float delta, final CallbackInfoReturnable<Float> ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().blindnessEffect.getValue())
            ci.setReturnValue(0F);
    }

}