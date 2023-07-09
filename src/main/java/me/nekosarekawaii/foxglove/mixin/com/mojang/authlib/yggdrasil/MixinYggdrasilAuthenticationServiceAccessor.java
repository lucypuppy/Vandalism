package me.nekosarekawaii.foxglove.mixin.com.mojang.authlib.yggdrasil;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(YggdrasilAuthenticationService.class)
public interface MixinYggdrasilAuthenticationServiceAccessor {
    @Accessor(remap = false)
    @Nullable String getClientToken();

    @Accessor(remap = false)
    @Mutable
    void setClientToken(String clientToken);
}