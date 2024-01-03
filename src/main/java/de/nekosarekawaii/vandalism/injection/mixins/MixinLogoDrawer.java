/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
