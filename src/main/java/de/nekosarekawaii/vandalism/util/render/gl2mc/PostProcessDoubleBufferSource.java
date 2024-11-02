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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.ESP2Module;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;

import java.awt.*;
import java.util.Optional;

public record PostProcessDoubleBufferSource(VertexConsumerProvider outlineBuffer, VertexConsumerProvider minecraftBufferSource, Color color) implements VertexConsumerProvider {

    @Override
    public VertexConsumer getBuffer(RenderLayer renderType) {
        if (renderType.isOutline()) {
            return VertexConsumers.union(this.outlineBuffer.getBuffer(renderType), this.minecraftBufferSource.getBuffer(renderType));
        } else {
            final Optional<RenderLayer> outline = renderType.getAffectedOutline();
            if (outline.isPresent()) {
                final ESP2Module esp = Vandalism.getInstance().getModuleManager().getEsp2Module();
                VertexConsumer consumer = this.outlineBuffer.getBuffer(esp.textureOutline.getValue() ? renderType : outline.get());
                if (!esp.textureOutline.getValue()) {
                    consumer = new CustomOutlineBufferSource.CustomOutlineVertexConsumer(consumer, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                }
                return VertexConsumers.union(consumer, this.minecraftBufferSource.getBuffer(renderType));
            }
            return this.minecraftBufferSource.getBuffer(renderType);
        }
    }

    public record WithFill(VertexConsumerProvider outlineBuffer, VertexConsumerProvider fillBuffer, VertexConsumerProvider minecraftBufferSource, Color outlineColor) implements VertexConsumerProvider {

        @Override
        public VertexConsumer getBuffer(RenderLayer renderType) {
            if (renderType.isOutline()) {
                return VertexConsumers.union(this.outlineBuffer.getBuffer(renderType), this.fillBuffer.getBuffer(renderType), this.minecraftBufferSource.getBuffer(renderType));
            } else {
                final Optional<RenderLayer> outline = renderType.getAffectedOutline();
                if (outline.isPresent()) {
                    final ESP2Module esp = Vandalism.getInstance().getModuleManager().getEsp2Module();
                    VertexConsumer outlineConsumer = this.outlineBuffer.getBuffer(esp.textureOutline.getValue() ? renderType : outline.get());
                    if (!esp.textureOutline.getValue()) {
                        outlineConsumer = new CustomOutlineBufferSource.CustomOutlineVertexConsumer(outlineConsumer, outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), outlineColor.getAlpha());
                    }
                    final boolean textureFill = esp.isFillTexture();
                    VertexConsumer fillConsumer = this.fillBuffer.getBuffer(textureFill ? renderType : outline.get());
                    if (!textureFill) {
                        fillConsumer = new CustomOutlineBufferSource.CustomOutlineVertexConsumer(fillConsumer, 255, 255, 255, (int) (esp.fillOpacity.getValue() * 255.0f));
                    }
                    return VertexConsumers.union(outlineConsumer, fillConsumer, this.minecraftBufferSource.getBuffer(renderType));
                }
                return this.minecraftBufferSource.getBuffer(renderType);
            }
        }
    }
}
