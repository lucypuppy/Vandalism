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

package de.nekosarekawaii.vandalism.util.render.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.util.render.gl.render.InstancedAttribConsumer;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.Passes;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class RenderUtil implements MinecraftWrapper {

    private static double FPS, PREV_GL_TIME = Double.NaN;

    public static void drawFrame() {
        if (Double.isNaN(PREV_GL_TIME)) {
            PREV_GL_TIME = GLFW.glfwGetTime();
            return;
        }
        final double time = GLFW.glfwGetTime();
        FPS = 1.0 / (time - PREV_GL_TIME);
        PREV_GL_TIME = time;
    }

    public static double getFps() {
        return FPS;
    }

    public static int getGlId(final Identifier identifier) {
        final AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(identifier);
        if (texture != null) {
            return texture.getGlId();
        } else {
            return -1;
        }
    }

    public static Color interpolateColor(final Color minColor, final Color midColor, final Color maxColor, final double percent) {
        if (minColor == null || midColor == null || maxColor == null) {
            throw new IllegalArgumentException("Color can't be null.");
        }
        if (percent <= 0.5) {
            return ColorUtils.colorInterpolate(minColor, midColor, MathHelper.clamp(percent * 2d, 0, 1));
        }
        return ColorUtils.colorInterpolate(midColor, maxColor, MathHelper.clamp((percent - 0.5) * 2d, 0, 1));
    }

    public static void fillOutlined(final DrawContext drawContext, final int x1, final int y1, final int x2, final int y2, final int outlineWidth, final int color, final int outlineColor) {
        drawContext.fill(
                x1 + outlineWidth,
                y1 + outlineWidth,
                x2 - outlineWidth,
                y2 - outlineWidth,
                color
        );
        drawContext.fill(
                x1,
                y1,
                x2,
                y1 + outlineWidth,
                outlineColor
        );
        drawContext.fill(
                x1,
                y2 - outlineWidth,
                x2,
                y2,
                outlineColor
        );
        drawContext.fill(
                x1,
                y1 + outlineWidth,
                x1 + outlineWidth,
                y2 - outlineWidth,
                outlineColor
        );
        drawContext.fill(
                x2 - outlineWidth,
                y1 + outlineWidth,
                x2,
                y2 - outlineWidth,
                outlineColor
        );
    }

    public static void fill(DrawContext context, RenderLayer layer, float x1, float y1, float x2, float y2, int z, int color) {
        final Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        final VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(layer);
        vertexConsumer.vertex(matrix4f, x1, y1, (float) z).color(color);
        vertexConsumer.vertex(matrix4f, x1, y2, (float) z).color(color);
        vertexConsumer.vertex(matrix4f, x2, y2, (float) z).color(color);
        vertexConsumer.vertex(matrix4f, x2, y1, (float) z).color(color);
        context.tryDraw();
    }

    public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
        fill(context, RenderLayer.getGui(), x1, y1, x2, y2, 0, color);
    }

    public static void fillWidth(DrawContext context, float x, float y, float width, float height, int color) {
        fill(context, RenderLayer.getGui(), x, y, x + width, y + height, 0, color);
    }

    public static void drawShaderRect(float x, float y, float x2, float y2) {
        try(final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            final InstancedAttribConsumer buffer = renderer.getAttribConsumers(Passes.rect()).main();
            buffer.pos(x, y2, 0.0F).next();
            buffer.pos(x2, y2, 0.0F).next();
            buffer.pos(x2, y, 0F).next();
            buffer.pos(x, y, 0.0F).next();
            renderer.drawWithoutShader();
        }
    }

    public static void drawShaderRect() {
        drawShaderRect(0.0F, 0.0F, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
        final ShaderProgram shader = Shaders.getRoundedRectangleShader();
        shader.bind();

        GlobalUniforms.setGlobalUniforms(shader, true);
        shader.uniform("u_size").set(width, height);
        shader.uniform("u_radius").set(radius);
        shader.uniform("u_color").set(color);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();

        drawShaderRect(x, y, x + width, y + height);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        shader.unbind();
    }

}
