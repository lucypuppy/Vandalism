package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.privacy;

import net.wurstclient.update.WurstUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WurstUpdater.class, remap = false)
public abstract class MixinWurstUpdater {

    @Inject(method = {"onUpdate", "checkForUpdates"}, at = @At("HEAD"), cancellable = true)
    private void cancelWurstUpdater(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "isOutdated", at = @At("HEAD"), cancellable = true)
    private void wurstIsNeverOutdated(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
