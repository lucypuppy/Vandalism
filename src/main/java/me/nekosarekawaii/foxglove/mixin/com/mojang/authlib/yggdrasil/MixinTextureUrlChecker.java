package me.nekosarekawaii.foxglove.mixin.com.mojang.authlib.yggdrasil;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ExploitFixerModule;
import me.nekosarekawaii.foxglove.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class MixinTextureUrlChecker {

    private final static String correctTextureUrlStart = "http://textures.minecraft.net/texture/";

    @Inject(method = "isAllowedTextureDomain", at = @At("HEAD"), cancellable = true)
    private static void injectIsAllowedTextureDomain(final String url, final CallbackInfoReturnable<Boolean> cir) {
        final ExploitFixerModule exploitFixerModule = Foxglove.getInstance().getFeatures().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.antiTextureDDoS.getValue()) {
            if (!StringUtils.startsWithIgnoreCase(url, correctTextureUrlStart)) {
                // Using this instead of the default logger to highlight it red and prevent possible RCEs.
                System.err.println("[" + Foxglove.getInstance().getName() + "] Game tried to load invalid Texture URL: " + url);
                cir.setReturnValue(false);
            }
        }
    }

}
