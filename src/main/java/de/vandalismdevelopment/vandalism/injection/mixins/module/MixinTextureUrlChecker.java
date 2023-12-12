package de.vandalismdevelopment.vandalism.injection.mixins.module;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.module.impl.exploit.ExploitFixerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void vandalism$exploitFixerBlockInvalidTextureUrls(final String url, final CallbackInfoReturnable<Boolean> cir) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.blockInvalidTextureUrls.getValue()) {
            final boolean startsWithUrl1 = StringUtils.startsWith(url, "https://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START);
            final boolean startsWithUrl2 = StringUtils.startsWith(url, "http://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START);
            if (!startsWithUrl1 && !startsWithUrl2) {
                cir.setReturnValue(false);
            }
        }
    }

}
