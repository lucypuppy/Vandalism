package me.nekosarekawaii.foxglove.injection.mixins;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ExploitFixerModule;
import me.nekosarekawaii.foxglove.util.string.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void injectIsAllowedTextureDomain(final String url, final CallbackInfoReturnable<Boolean> cir) {
        final ExploitFixerModule exploitFixerModule = Foxglove.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidTextureUrls.getValue()) {
            if (
                    !StringUtils.startsWithIgnoreCase(url, "https://" + exploitFixerModule.correctTextureUrlStart) &&
                            !StringUtils.startsWithIgnoreCase(url, "http://" + exploitFixerModule.correctTextureUrlStart)
            ) {
                cir.setReturnValue(false);
            }
        }
    }

}
