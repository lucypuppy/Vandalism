package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins;

import net.wurstclient.WurstInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WurstInitializer.class, remap = false)
public abstract class MixinWurstInitializer {

    @Inject(method = "onInitialize", at = @At("HEAD"), cancellable = true)
    private void cancelWurstInitialization(final CallbackInfo ci) {
        ci.cancel(); // Cancel Wurst initialization because we are moving it to load after the client has been initialized, counterpart in AddonWurstClient.java
    }

}
