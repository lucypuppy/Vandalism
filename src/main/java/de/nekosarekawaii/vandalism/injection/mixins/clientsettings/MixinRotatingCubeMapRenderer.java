/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.clientsettings;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.render.Shaders;
import de.nekosarekawaii.vandalism.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
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
            final ShaderProgram shader = Shaders.getDarkNightBackgroundShader();
            shader.bind();
            GlobalUniforms.setBackgroundUniforms(shader, menuSettings.shaderColor1.getColor(), menuSettings.shaderColor2.getColor(), menuSettings.shaderColor3.getColor());
            final Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            bufferBuilder.vertex(matrix, -1F, -1F, 0F).next();
            bufferBuilder.vertex(matrix, client.getWindow().getFramebufferWidth(), -1F, 0F).next();
            bufferBuilder.vertex(matrix, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), 0F).next();
            bufferBuilder.vertex(matrix, -1F, client.getWindow().getFramebufferHeight(), 0F).next();
            BufferRenderer.draw(bufferBuilder.end());
            shader.unbind();
        }
    }

}
