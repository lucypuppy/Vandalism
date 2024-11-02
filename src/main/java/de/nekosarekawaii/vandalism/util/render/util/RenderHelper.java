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

package de.nekosarekawaii.vandalism.util.render.util;

import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumer;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.Passes;
import org.joml.Matrix4fc;

public class RenderHelper {

    public static void roundedRect(AttribConsumer consumer, Matrix4fc transform, float left, float top, float right, float bottom, int color, float topLeftRadius, int topLeftNumVerticesPerEdge, float topRightRadius, int topRightNumVerticesPerEdge, float bottomRightRadius, int bottomRightNumVerticesPerEdge, float bottomLeftRadius, int bottomLeftNumVerticesPerEdge) {
        final float centerX = (left + right) / 2.0f;
        final float centerY = (top + bottom) / 2.0f;
        consumer.pos(transform, centerX, centerY, 0.0f).putColor8(color).next();

        // top-left
        roundedRectEdge(consumer, transform, left + topLeftRadius, top + topLeftRadius, topLeftRadius, topLeftNumVerticesPerEdge, color, 2.0);

        // bottom-left
        roundedRectEdge(consumer, transform, left + bottomLeftRadius, bottom - bottomLeftRadius, bottomLeftRadius, bottomLeftNumVerticesPerEdge, color, 1.0);

        // bottom-right
        roundedRectEdge(consumer, transform, right - bottomRightRadius, bottom - bottomRightRadius, bottomRightRadius, bottomRightNumVerticesPerEdge, color, 0.0);

        // top-right
        roundedRectEdge(consumer, transform, right - topRightRadius, top + topRightRadius, topRightRadius, topRightNumVerticesPerEdge, color, 3.0);

        consumer.pos(transform, left + topLeftRadius, top, 0.0f).putColor8(color).next();

        consumer.nextConnectedPrimitive();
    }

    public static void roundedRect(AttribConsumerProvider consumer, Matrix4fc transform, float left, float top, float right, float bottom, int color, float topLeftRadius, int topLeftNumVerticesPerEdge, float topRightRadius, int topRightNumVerticesPerEdge, float bottomRightRadius, int bottomRightNumVerticesPerEdge, float bottomLeftRadius, int bottomLeftNumVerticesPerEdge) {
        roundedRect(consumer.getAttribConsumers(Passes.colorTriangleFan()).main(), transform, left, top, right, bottom, color, topLeftRadius, topLeftNumVerticesPerEdge, topRightRadius, topRightNumVerticesPerEdge, bottomRightRadius, bottomRightNumVerticesPerEdge, bottomLeftRadius, bottomLeftNumVerticesPerEdge);
    }

    private static void roundedRectEdge(AttribConsumer consumer, Matrix4fc transform, float x, float y, float radius, int numVerticesPerEdge, int color, double angleOffset) {
        final double ninetyDegrees = Math.PI / 2.0;
        for (int i = numVerticesPerEdge; i >= 0; --i) {
            double angle = ninetyDegrees * i / numVerticesPerEdge + ninetyDegrees * angleOffset;
            consumer.pos(transform, x + (float) Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius, 0.0f).putColor8(color).next();
        }
    }

    public static void roundedRect(AttribConsumer consumer, Matrix4fc transform, float left, float top, float right, float bottom, int color, float radius, int numVerticesPerEdge) {
        roundedRect(consumer, transform, left, top, right, bottom, color, radius, numVerticesPerEdge, radius, numVerticesPerEdge, radius, numVerticesPerEdge, radius, numVerticesPerEdge);
    }

    public static void roundedRect(AttribConsumerProvider batch, Matrix4fc transform, float left, float top, float right, float bottom, int color, float radius, int numVerticesPerEdge) {
        roundedRect(batch.getAttribConsumers(Passes.colorTriangleFan()).main(), transform, left, top, right, bottom, color, radius, numVerticesPerEdge);
    }
}
