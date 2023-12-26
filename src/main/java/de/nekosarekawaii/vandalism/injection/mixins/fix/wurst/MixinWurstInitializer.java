package de.nekosarekawaii.vandalism.injection.mixins.fix.wurst;

import net.wurstclient.WurstInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WurstInitializer.class, remap = false)
public abstract class MixinWurstInitializer {

    @Inject(method = "onInitialize", at = @At("HEAD"), cancellable = true)
    private void cancelWurstInitialization(final CallbackInfo ci) {
        ci.cancel();
    }

}
