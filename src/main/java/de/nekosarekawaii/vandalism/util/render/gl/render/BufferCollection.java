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

package de.nekosarekawaii.vandalism.util.render.gl.render;

import de.nekosarekawaii.vandalism.util.render.gl.mem.BufferPool;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.RenderPass;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayout;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class BufferCollection implements AttribConsumerSet, AutoCloseable {

    private final BufferPool bufferPool;
    private final RenderPass pass;
    private final VertexLayout mainLayout;
    private BufferAttribConsumer mainBuffer;
    private BufferIndexConsumer indexDataBuffer;
    private final Object2ObjectMap<VertexLayout, BufferAttribConsumer> customBuffers;

    public BufferCollection(BufferPool bufferPool, RenderPass pass) {
        this.bufferPool = bufferPool;
        this.pass = pass;
        this.mainLayout = pass.getVertexLayout();
        this.customBuffers = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public @NotNull InstancedAttribConsumer main() {
        if (this.mainBuffer == null) {
            this.mainBuffer = new BufferAttribConsumer(this.bufferPool.borrowBuffer());
        }
        return this.mainBuffer;
    }

    @Override
    public @NotNull IndexConsumer indexData(IndexType type) {
        if (this.indexDataBuffer == null) {
            this.indexDataBuffer = new BufferIndexConsumer(this, this.bufferPool.borrowBuffer(), type);
        } else if (type != this.indexDataBuffer.getType()) {
            throw new IllegalStateException("Cannot change index type after it has been set");
        }
        if (this.mainBuffer != null) {
            this.indexDataBuffer.setBaseOffset(this.mainBuffer.getVertexNumNextCalls());
        }
        return this.indexDataBuffer;
    }

    @Override
    public @NotNull AttribConsumer custom(@NotNull VertexLayout layout) {
        return this.customBuffers.computeIfAbsent(layout, l -> new BufferAttribConsumer(this.bufferPool.borrowBuffer()));
    }

    public void reset() {
        if (this.mainBuffer != null) {
            this.bufferPool.returnBuffer(this.mainBuffer.getBuffer());
            this.mainBuffer = null;
        }
        if (this.indexDataBuffer != null) {
            this.bufferPool.returnBuffer(this.indexDataBuffer.getBuffer());
            this.indexDataBuffer = null;
        }
        for (BufferAttribConsumer builder : this.customBuffers.values()) {
            this.bufferPool.returnBuffer(builder.getBuffer());
        }
        this.customBuffers.clear();
    }

    @Override
    public void close() {
        this.reset();
    }
}
