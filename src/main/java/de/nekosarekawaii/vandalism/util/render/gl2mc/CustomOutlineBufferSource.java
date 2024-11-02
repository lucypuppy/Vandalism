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

package de.nekosarekawaii.vandalism.util.render.gl2mc;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;
import java.util.Optional;

public class CustomOutlineBufferSource implements VertexConsumerProvider {

    private final VertexConsumerProvider.Immediate outlineBufferSource = VertexConsumerProvider.immediate(Tessellator.getInstance().allocator);
    @Getter
    @Setter
    private Color color = Color.WHITE;

    @Override
    public VertexConsumer getBuffer(RenderLayer renderType) {
        if (renderType.isOutline()) {
            return new CustomOutlineVertexConsumer(outlineBufferSource.getBuffer(renderType), this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha());
        } else {
            final Optional<RenderLayer> outline = renderType.getAffectedOutline();
            if (outline.isPresent()) {
                return new CustomOutlineVertexConsumer(outlineBufferSource.getBuffer(outline.get()), this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha());
            }
            return SinkVertexConsumer.INSTANCE;
        }
    }

    public void endOutlineBatch() {
        this.outlineBufferSource.draw();
    }

    public record CustomOutlineVertexConsumer(VertexConsumer delegate, int color) implements VertexConsumer {

        public CustomOutlineVertexConsumer(VertexConsumer delegate, int red, int green, int blue, int alpha) {
            this(delegate, ColorHelper.Argb.getArgb(alpha, red, green, blue));
        }

        public VertexConsumer vertex(float x, float y, float z) {
            this.delegate.vertex(x, y, z).color(this.color);
            return this;
        }

        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        public VertexConsumer texture(float u, float v) {
            this.delegate.texture(u, v);
            return this;
        }

        public VertexConsumer overlay(int u, int v) {
            return this;
        }

        public VertexConsumer light(int u, int v) {
            return this;
        }

        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        public VertexConsumer delegate() {
            return this.delegate;
        }

        public int color() {
            return this.color;
        }
    }
}
