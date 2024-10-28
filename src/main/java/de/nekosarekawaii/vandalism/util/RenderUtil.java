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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public class RenderUtil {

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

}
