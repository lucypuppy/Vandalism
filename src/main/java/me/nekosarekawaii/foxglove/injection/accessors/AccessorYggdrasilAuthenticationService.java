package me.nekosarekawaii.foxglove.injection.accessors;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(YggdrasilAuthenticationService.class)
public interface AccessorYggdrasilAuthenticationService {

    @Accessor(remap = false)
    @Nullable String getClientToken();

    @Accessor(remap = false)
    @Mutable
    void setClientToken(final String clientToken);

}