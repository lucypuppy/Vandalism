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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.MenuSettings;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
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

    @Shadow protected abstract void applyBlur(float delta);

    @ModifyConstant(method = "keyPressed", constant = @Constant(intValue = GLFW.GLFW_KEY_ESCAPE))
    private int modifyEscapeKey(int constant) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();
        if (menuSettings.changeScreenCloseButton.getValue()) {
            constant = menuSettings.changeScreenCloseButtonKey.getValue();
        }
        return constant;
    }

    @Inject(method = "renderInGameBackground", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackgroundInGame(final DrawContext context, final CallbackInfo ci) {
        final MenuSettings menuSettings = Vandalism.getInstance().getClientSettings().getMenuSettings();

        if (menuSettings.inGameBackgroundBlur.getValue())
            applyBlur(client.getRenderTickCounter().getTickDelta(false));

        switch (menuSettings.inGameBackgroundMode.getValue()) {
            case COLOR_FADE -> {
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

            case SHADER -> {
                ci.cancel();
                final Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
                final ShaderProgram backgroundShader = Shaders.getIngameGuiBackgroundShader();

                backgroundShader.bind();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.disableDepthTest();

                GlobalUniforms.setBackgroundUniforms(backgroundShader);
                backgroundShader.uniform("sparkColor").set(menuSettings.shaderColorSpark.getColor(), false);
                backgroundShader.uniform("bloomColor").set(menuSettings.shaderColorBloom.getColor(), false);
                backgroundShader.uniform("smokeColor").set(menuSettings.shaderColorSmoke.getColor(), false);

                backgroundShader.uniform("size").set(6.28f);
                backgroundShader.uniform("fadeDivision").set(1.0f);

                final BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                bufferBuilder.vertex(matrix, -1F, -1F, 0F);
                bufferBuilder.vertex(matrix, client.getWindow().getFramebufferWidth(), -1F, 0F);
                bufferBuilder.vertex(matrix, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), 0F);
                bufferBuilder.vertex(matrix, -1F, client.getWindow().getFramebufferHeight(), 0F);
                BufferRenderer.draw(bufferBuilder.end());

                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
                backgroundShader.unbind();
            }
        }
    }

}
