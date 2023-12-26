package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.analytics.WurstAnalytics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WurstAnalytics.class, remap = false)
public abstract class MixinWurstAnalytics {

    @Inject(method = "isEnabled", at = @At("HEAD"), cancellable = true)
    private void cancelWurstAnalytics(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "setEnabled", at = @At("HEAD"), cancellable = true)
    public void cancelWurstAnalytics(final boolean enabled, final CallbackInfo ci) {
        ci.cancel();
    }

}
