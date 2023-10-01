package de.vandalismdevelopment.vandalism.injection.mixins.minecraft;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ExploitFixerModule;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerSkinProvider.Key.class)
public abstract class MixinPlayerSkinProvider {

    @Shadow
    @Final
    private GameProfile profile;

    @Redirect(method = "equals", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getId()Ljava/util/UUID;", ordinal = 0))
    private UUID redirectEqualsUUID1(final GameProfile instance) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidPlayerSkinCrash.getValue() && this.profile == null) {
            return ExploitFixerModule.PLACEHOLDER_GAME_PROFILE.getId();
        }
        return instance.getId();
    }

    @Redirect(method = "equals", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getId()Ljava/util/UUID;", ordinal = 1))
    private UUID redirectEqualsUUID2(final GameProfile instance) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidPlayerSkinCrash.getValue() && instance == null) {
            return ExploitFixerModule.PLACEHOLDER_GAME_PROFILE.getId();
        }
        return instance.getId();
    }

    @Inject(method = "hashCode", at = @At(value = "HEAD"), cancellable = true)
    private void injectHashCode(final CallbackInfoReturnable<Integer> cir) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidPlayerSkinCrash.getValue() && this.profile == null) {
            cir.setReturnValue(ExploitFixerModule.PLACEHOLDER_GAME_PROFILE.getId().hashCode());
        }
    }

    @Redirect(method = "getTextureEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinProvider;getTextureEntry(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/properties/Property;"))
    private Property injectGetTextureEntry(final GameProfile profile) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        final boolean usePlaceHolder = exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidPlayerSkinCrash.getValue() && this.profile == null;
        return Iterables.getFirst((usePlaceHolder ? ExploitFixerModule.PLACEHOLDER_GAME_PROFILE : profile).getProperties().get("textures"), null);
    }

    @Inject(method = "profile", at = @At(value = "HEAD"), cancellable = true)
    private void injectProfile(final CallbackInfoReturnable<GameProfile> cir) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockInvalidPlayerSkinCrash.getValue() && this.profile == null) {
            cir.setReturnValue(ExploitFixerModule.PLACEHOLDER_GAME_PROFILE);
        }
    }

}
