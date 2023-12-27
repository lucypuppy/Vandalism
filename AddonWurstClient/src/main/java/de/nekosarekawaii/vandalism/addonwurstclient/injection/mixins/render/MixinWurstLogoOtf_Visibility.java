package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.render;

import net.wurstclient.other_features.WurstLogoOtf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WurstLogoOtf.Visibility.class, remap = false)
public abstract class MixinWurstLogoOtf_Visibility {

    @Inject(method = "toString", at = @At("RETURN"), cancellable = true)
    private void renameWurstLogoSetting(final CallbackInfoReturnable<String> cir) {
        if (cir.getReturnValue().contains("outdated")) {
            cir.setReturnValue("Never");
        }
    }

}
