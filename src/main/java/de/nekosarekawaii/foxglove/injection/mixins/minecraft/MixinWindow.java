package de.nekosarekawaii.foxglove.injection.mixins.minecraft;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class MixinWindow {

    @Inject(method = "logGlError", at = @At("HEAD"))
    public void injectLogGlError(final int error, final long description, final CallbackInfo ci) {
        new IllegalStateException().printStackTrace();
    }

}
