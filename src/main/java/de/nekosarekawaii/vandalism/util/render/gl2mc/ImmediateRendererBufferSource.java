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

import de.nekosarekawaii.vandalism.util.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.RenderPass;
import lombok.Getter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.LinkedHashSet;
import java.util.Set;

public class ImmediateRendererBufferSource extends VertexConsumerProvider.Immediate {

    @Getter
    private final ImmediateRenderer renderer;
    @Getter
    private final AttribConsumerProviderMultiBufferSource bufferSourceWrapper;
    private final Set<RenderLayer> usedRenderTypes = new LinkedHashSet<>();
    private RenderLayer lastRenderType;

    public ImmediateRendererBufferSource(ImmediateRenderer renderer) {
        super(null, null);
        this.renderer = renderer;
        this.bufferSourceWrapper = new AttribConsumerProviderMultiBufferSource(renderer);
    }

    public void reset() {
        this.lastRenderType = null;
        this.bufferSourceWrapper.reset();
        for (RenderLayer renderType : this.usedRenderTypes) {
            this.renderer.drawPass(this.bufferSourceWrapper.getRenderPass(renderType));
        }
        this.usedRenderTypes.clear();
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer renderType) {
        this.lastRenderType = renderType;
        this.usedRenderTypes.add(renderType);
        return this.bufferSourceWrapper.getBuffer(renderType);
    }

    @Override
    public void drawCurrentLayer() {
        if (this.lastRenderType == null) return;
        this.renderer.drawPass(this.bufferSourceWrapper.getRenderPass(this.lastRenderType));
        this.usedRenderTypes.remove(this.lastRenderType);
        this.lastRenderType = null;
    }

    @Override
    public void draw() {
        for (RenderLayer renderType : this.usedRenderTypes) {
            this.renderer.drawPass(this.bufferSourceWrapper.getRenderPass(renderType));
        }
        this.usedRenderTypes.clear();
        this.bufferSourceWrapper.reset();
        this.lastRenderType = null;
    }

    @Override
    public void draw(RenderLayer renderType) {
        this.usedRenderTypes.remove(renderType);
        if (renderType == this.lastRenderType) this.lastRenderType = null;
        final RenderPass pass = this.bufferSourceWrapper.getRenderPass(renderType);
        if (pass == null) return;
        this.renderer.drawPass(pass);
    }
}
