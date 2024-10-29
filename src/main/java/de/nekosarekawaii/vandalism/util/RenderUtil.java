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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class RenderUtil implements MinecraftWrapper {

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
        final float guiWidth = mc.getWindow().getScaledWidth();
        final float guiHeight = mc.getWindow().getScaledHeight();
        final float rectLeft = (x / guiWidth) * 2.0f - 1.0f;
        final float rectRight = (x2 / guiWidth) * 2.0f - 1.0f;
        final float rectTop = 1.0f - (y / guiHeight) * 2.0f;
        final float rectBottom = 1.0f - (y2 / guiHeight) * 2.0f;

        final BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bb.vertex(rectLeft, rectBottom, 0.0f);
        bb.vertex(rectRight, rectBottom, 0.0f);
        bb.vertex(rectRight, rectTop, 0.0f);
        bb.vertex(rectLeft, rectTop, 0.0f);
        BufferRenderer.draw(bb.end());
    }

}
