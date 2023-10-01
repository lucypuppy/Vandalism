package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import com.mojang.authlib.GameProfile;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.client.util.DefaultSkinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(DefaultSkinHelper.class)
public abstract class MixinDefaultSkinHelper {

    @Redirect(method = "getTexture(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/util/SkinTextures;", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getId()Ljava/util/UUID;"))
    private static UUID redirectGetTexture(GameProfile instance) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidPlayerSkinCrash.getValue() && instance == null) {
            return ExploitFixerModule.PLACEHOLDER_GAME_PROFILE.getId();
        }
        return instance.getId();
    }

}
