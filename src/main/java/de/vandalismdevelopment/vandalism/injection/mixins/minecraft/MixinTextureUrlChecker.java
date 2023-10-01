package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ExploitFixerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    //TODO: Make better fix since that this could cause issues.
    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void injectIsAllowedTextureDomain(final String url, final CallbackInfoReturnable<Boolean> cir) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidTextureUrls.getValue()) {
            if (
                    !StringUtils.startsWith(url, "https://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START) &&
                            !StringUtils.startsWith(url, "http://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START)
            ) {
                cir.setReturnValue(false);
            }
        }
    }

}
