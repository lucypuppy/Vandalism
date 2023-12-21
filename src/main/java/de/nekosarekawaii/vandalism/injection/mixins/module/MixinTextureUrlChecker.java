package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.ExploitFixerModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void hookExploitFixer(final String url, final CallbackInfoReturnable<Boolean> cir) {
        final var exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();

        if (exploitFixerModule.isActive() && exploitFixerModule.blockInvalidTextureUrls.getValue()) {
            if (!url.toLowerCase().startsWith("https://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START) && !url.toLowerCase().startsWith("http://" + ExploitFixerModule.CORRECT_TEXTURE_URL_START)) {
                cir.setReturnValue(false);
            }
        }
    }

}
