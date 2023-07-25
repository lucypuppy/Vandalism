package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.util;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class MixinWindow {

    @Inject(method = "logGlError", at = @At("HEAD"))
    public void throwGLExceptions(int error, long description, CallbackInfo ci) {
        new IllegalStateException().printStackTrace();
    }
}
