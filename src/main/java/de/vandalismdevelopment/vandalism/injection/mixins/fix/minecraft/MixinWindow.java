package de.vandalismdevelopment.vandalism.injection.mixins.fix.minecraft;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class MixinWindow {

    @Inject(method = "logGlError", at = @At("HEAD"))
    public void vandalism$forceLogGlErrors(final int error, final long description, final CallbackInfo ci) {
        Vandalism.getInstance().getLogger().error("GL Error: " + error + " " + description);
    }

}
