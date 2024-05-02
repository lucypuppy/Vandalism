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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = GLFW.GLFW_KEY_ESCAPE))
    private int modifyEscapeKey(int constant) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.changeScreenCloseButton.getValue()) {
            constant = menuSettings.changeScreenCloseButtonKey.getValue();
        }
        return constant;
    }

    @Inject(method = "renderBackground", at = @At(value = "HEAD"), cancellable = true)
    private void drawCustomBackgroundInGui(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();

        if (this.client.world != null) return;

        if (menuSettings.backgroundMode.getValue() == MenuSettings.BackgroundMode.COLOR) {
            ci.cancel();
            context.fill(
                    0,
                    0,
                    this.width,
                    this.height,
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
            bufferBuilder.vertex(matrix, client.getWindow().getFramebufferWidth(),
                    client.getWindow().getFramebufferHeight(), 0F).next();
            bufferBuilder.vertex(matrix, -1F, client.getWindow().getFramebufferHeight(), 0F).next();
            BufferRenderer.draw(bufferBuilder.end());

            shader.unbind();
        }
    }

    @Inject(method = "renderInGameBackground", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackgroundInGame(final DrawContext context, final CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.inGameCustomBackground.getValue()) {
            ci.cancel();
            context.fillGradient(
                    0,
                    0,
                    this.width,
                    this.height,
                    menuSettings.inGameCustomBackgroundColorTop.getColor().getRGB(),
                    menuSettings.inGameCustomBackgroundColorBottom.getColor().getRGB()
            );
        }
    }

}
