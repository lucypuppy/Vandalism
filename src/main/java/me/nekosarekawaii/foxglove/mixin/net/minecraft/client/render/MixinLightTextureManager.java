package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.render;

import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
public class MixinLightTextureManager {

    @Inject(method = "getDarknessFactor", at = @At("HEAD"), cancellable = true)
    private void injectGetDarknessFactor(final float delta, final CallbackInfoReturnable<Float> ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().blindnessEffect.getValue())
            ci.setReturnValue(0F);
    }

}