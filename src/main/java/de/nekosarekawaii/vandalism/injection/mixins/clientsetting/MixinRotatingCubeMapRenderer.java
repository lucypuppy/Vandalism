/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotatingCubeMapRenderer.class)
public abstract class MixinRotatingCubeMapRenderer {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackground(final DrawContext context, final int width, final int height, final float alpha, final float tickDelta, final CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.backgroundMode.getValue() == MenuSettings.BackgroundMode.COLOR) {
            ci.cancel();
            context.fill(
                    0,
                    0,
                    width,
                    height,
                    menuSettings.customBackgroundColor.getColor().getRGB()
            );
        } else if (menuSettings.backgroundMode.getValue() == MenuSettings.BackgroundMode.SHADER) {
            ci.cancel();
            ShaderProgram shader;
            switch (this.client.currentScreen) {
                case TitleScreen titleScreen -> shader = Shaders.getTitleScreenBackgroundShader();
                case DownloadingTerrainScreen downloadingTerrainScreen -> shader = Shaders.getLoadingScreenBackgroundShader();
                case ConnectScreen connectScreen -> shader = Shaders.getLoadingScreenBackgroundShader();
                case ReconfiguringScreen reconfiguringScreen -> shader = Shaders.getLoadingScreenBackgroundShader();
                case null, default -> shader = Shaders.getBackgroundShader();
            }

            shader.bind();
            GlobalUniforms.setGlobalUniforms(shader, true);
            RenderUtil.drawShaderRect();
            shader.unbind();
        }
    }

}
