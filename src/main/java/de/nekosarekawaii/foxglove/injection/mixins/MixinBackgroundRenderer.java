package de.nekosarekawaii.foxglove.injection.mixins;

import de.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.StatusEffectFogModifier;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {

    @Inject(method = "getFogModifier", at = @At("HEAD"), cancellable = true)
    private static void injectGetFogModifier(final Entity entity, final float tickDelta, final CallbackInfoReturnable<StatusEffectFogModifier> ci) {
        if (!Foxglove.getInstance().getConfigManager().getMainConfig().blindnessEffect.getValue())
            ci.setReturnValue(null);
    }

}