package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LogoDrawer.class)
public abstract class MixinLogoDrawer {

    @Shadow
    @Final
    public static Identifier LOGO_TEXTURE;

    @Shadow
    @Final
    public static Identifier EDITION_TEXTURE;

    @Shadow
    @Final
    public static Identifier MINCERAFT_TEXTURE;

    @Redirect(method = "draw(Lnet/minecraft/client/gui/DrawContext;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void forceModLogo(final DrawContext instance, final Identifier texture, final int x, final int y, final float u, final float v, final int width, final int height, final int textureWidth, final int textureHeight) {
        if (texture.equals(LOGO_TEXTURE) || texture.equals(MINCERAFT_TEXTURE)) {
            instance.drawTexture(FabricBootstrap.MOD_ICON, x, y, u, v, width, height, textureWidth, textureHeight);
        } else if (!texture.equals(EDITION_TEXTURE)) {
            instance.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
        }
    }

}
