package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen {

    @Redirect(method = "loadTexturesAsync", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;loadTextureAsync(Lnet/minecraft/util/Identifier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static CompletableFuture<Void> vandalism$forceModLogo(final TextureManager instance, final Identifier id, final Executor executor) {
        final Identifier newId;
        if (id.equals(LogoDrawer.LOGO_TEXTURE)) newId = FabricBootstrap.MOD_ICON;
        else if (id.equals(LogoDrawer.EDITION_TEXTURE)) return CompletableFuture.completedFuture(null);
        else newId = id;
        return instance.loadTextureAsync(newId, executor);
    }

}
