package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.analytics.dmurph.JGoogleAnalyticsTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JGoogleAnalyticsTracker.class, remap = false)
public abstract class MixinJGoogleAnalyticsTracker {

    @Inject(method = {"startBackgroundThread", "createBuilder"}, at = @At("HEAD"), cancellable = true)
    private void cancelWurstJGoogleAnalyticsTrackerThread(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "stopBackgroundThread", at = @At("HEAD"), cancellable = true)
    private static void cancelWurstJGoogleAnalyticsTrackerStopThread(final CallbackInfo ci) {
        ci.cancel();
    }

}
