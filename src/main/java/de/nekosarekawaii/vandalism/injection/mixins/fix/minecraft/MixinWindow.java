package de.nekosarekawaii.vandalism.injection.mixins.fix.minecraft;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class MixinWindow {

    @Inject(method = "logGlError", at = @At("HEAD"))
    public void vandalism$forceLogGlErrors(final int error, final long description, final CallbackInfo ci) {
        // Causes the JVM to actually show the caller tree of the native method, added in MC 1.13 with LWJGL 3 we can finally see
        // Proper stack traces when calling native gl functions
        new IllegalStateException().printStackTrace();
    }

}
