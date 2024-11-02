/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.integration;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    @Shadow
    public abstract void renderBackground(final DrawContext context, final int mouseX, final int mouseY, final float delta);

    protected MixinTitleScreen(final Text title) {
        super(title);
    }

    @Redirect(method = "loadTexturesAsync", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;loadTextureAsync(Lnet/minecraft/util/Identifier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static CompletableFuture<Void>forceClientLogo(final TextureManager instance, final Identifier id, final Executor executor) {
        final Identifier newId;
        if (id.equals(LogoDrawer.LOGO_TEXTURE)) newId = FabricBootstrap.MOD_LOGO;
        else if (id.equals(LogoDrawer.EDITION_TEXTURE)) return CompletableFuture.completedFuture(null);
        else newId = id;
        return instance.loadTextureAsync(newId, executor);
    }

}
