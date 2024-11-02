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

import de.nekosarekawaii.vandalism.util.render.gl.buffer.BufferObject;
import de.nekosarekawaii.vandalism.util.render.gl.mem.BufferPool;
import de.nekosarekawaii.vandalism.util.render.gl.mem.ByteBufferBuilder;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.RenderPass;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VAO;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayout;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PersistentMeshProducer implements AttribConsumerProvider, AutoCloseable {

    private final Object2ObjectMap<RenderPass, BufferCollection> usedBuffers = new Object2ObjectLinkedOpenHashMap<>();
    private final BufferPool bufferPool;
    private RenderPass lastPass;
    private BufferCollection lastBuffers;

    public PersistentMeshProducer(BufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override
    public AttribConsumerSet getAttribConsumers(RenderPass pass) {
        if (this.lastPass == pass) return this.lastBuffers;
        final BufferCollection buffers = this.usedBuffers.computeIfAbsent(pass, k -> new BufferCollection(this.bufferPool, pass));
        this.lastPass = pass;
        this.lastBuffers = buffers;
        return buffers;
    }

    public PersistentMesh buildMesh() {
        return this.buildMesh(Collections.emptyList());
    }

    public PersistentMesh buildMesh(Collection<RenderPass> orderedPasses) {
        this.lastPass = null;
        this.lastBuffers = null;
        final List<PersistentMesh.PassEntry> passes = new ArrayList<>();
        for (RenderPass pass : orderedPasses) {
            final BufferCollection buffers = this.usedBuffers.remove(pass);
            if (buffers == null) continue;
            this.addPass(pass, buffers, passes);
        }
        for (Map.Entry<RenderPass, BufferCollection> entry : this.usedBuffers.entrySet()) {
            this.addPass(entry.getKey(), entry.getValue(), passes);
        }
        this.reset();
        return new PersistentMesh(passes);
    }

    private void addPass(RenderPass pass, BufferCollection buffers, List<PersistentMesh.PassEntry> passes) {
        int bufferCount = 1;
        bufferCount += buffers.getCustomBuffers().size();

        final BufferAttribConsumer mainBuffer = buffers.getMainBuffer();
        final VertexLayout instanceLayout = pass.getInstanceLayout();
        final boolean instance = instanceLayout != null && mainBuffer.isInstanced();
        final long instanceDataOffset = mainBuffer.getInstanceDataOffset();
        final int instanceCount = instance ? mainBuffer.getInstanceCount() : 1;

        final boolean connectedPrimitives = pass.getPrimitiveType().isConnectedPrimitive();
        final int multiDrawCount = connectedPrimitives ? mainBuffer.getMultiDrawCount() : -1;
        final int[] multiDrawOffsets, multiDrawCounts;
        if (multiDrawCount != -1) {
            multiDrawOffsets = new int[multiDrawCount];
            multiDrawCounts = new int[multiDrawCount];
            mainBuffer.getMultiDrawOffsetsAndCounts(multiDrawOffsets, multiDrawCounts);
        } else multiDrawCounts = multiDrawOffsets = null;
        final long indirectDataOffset;

        final BufferObject[] bufferObjects = new BufferObject[bufferCount];
        BufferObject elementBuffer = null;
        final VAO vao = new VAO();
        boolean successful = false;
        try {
            int vertexCount = buffers.getMainBuffer().getVertexNumNextCalls();
            int indexCount = 0;
            IndexType indexType = null;

            boolean hasElementBuffer = buffers.getIndexDataBuffer() != null;
            if (hasElementBuffer) {
                indexCount = buffers.getIndexDataBuffer().getIndexCount();
                elementBuffer = buffers.getIndexDataBuffer().getBuffer().uploadImmutable(0);
                indexType = buffers.getIndexDataBuffer().getType();
                vao.setElementBuffer(elementBuffer);
            } else if (pass.getPrimitiveType() == PrimitiveType.QUADS) {
                elementBuffer = GlobalQuadElementBuffer.getQuadElementBuffer(vertexCount / 4);
                indexType = GlobalQuadElementBuffer.QUAD_ELEMENT_BUFFER_TYPE;
                vao.setElementBuffer(elementBuffer);
                elementBuffer = null; // the VAO shouldn't own the buffer
                hasElementBuffer = true;
                vertexCount /= 4;
                vertexCount *= 6;
                indexCount = vertexCount;
            }

            final BufferObject commandBuffer;
            {
                final ByteBufferBuilder buf = mainBuffer.getBuffer();
                while (buf.written() % 4 != 0) buf.putByte((byte) 0);
                indirectDataOffset = buf.written();
                if (multiDrawCount == -1) {
                    if (hasElementBuffer) IndirectCommands.writeDrawElementsIndirect(buf, indexCount, instanceCount, 0, 0, 0);
                    else IndirectCommands.writeDrawArraysIndirect(buf, vertexCount, instanceCount, 0, 0);
                } else {
                    if (hasElementBuffer) {
                        //for (int i = 0; i < multiDrawCount; i++) IndirectCommands.writeDrawElementsIndirect(buf, multiDrawCounts[i], instanceCount, 0, 0, 0);
                        throw new UnsupportedOperationException("MultiDrawElementsIndirect is not supported with element buffers");
                    } else {
                        for (int i = 0; i < multiDrawCount; i++) IndirectCommands.writeDrawArraysIndirect(buf, multiDrawCounts[i], instanceCount, multiDrawOffsets[i], 0);
                    }
                }

                final BufferObject mainBuf = mainBuffer.getBuffer().uploadImmutable(0);
                vao.bindVertexBufferAndLayout(0, mainBuf, 0L, pass.getVertexLayout());
                bufferObjects[0] = mainBuf;
                commandBuffer = mainBuf;
            }
            int bindingIndex = 1;
            for (Map.Entry<VertexLayout, BufferAttribConsumer> entry : buffers.getCustomBuffers().entrySet()) {
                final BufferObject buffer = entry.getValue().getBuffer().uploadImmutable(0);
                vao.bindVertexBufferAndLayout(bindingIndex, buffer, 0L, entry.getKey());
                bufferObjects[bindingIndex] = buffer;
                ++bindingIndex;
            }
            if (instance) {
                vao.bindVertexBufferAndLayout(bindingIndex, bufferObjects[0], instanceDataOffset, instanceLayout);
            }

            passes.add(new PersistentMesh.PassEntry(pass, vao, elementBuffer, buffers.getIndexDataBuffer() != null, indexType, bufferObjects, commandBuffer, indirectDataOffset, hasElementBuffer, multiDrawCount, mainBuffer.getInstanceCount()));
            successful = true;
        } finally {
            if (!successful) {
                vao.close();
            }
        }
    }

    /**
     * Resets the mesh producer. All buffers will be reset and be ready for reuse.
     */
    public void reset() {
        this.lastPass = null;
        this.lastBuffers = null;
        for (BufferCollection buffers : this.usedBuffers.values()) {
            buffers.reset();
        }
        this.usedBuffers.clear();
    }

    /**
     * Frees all buffers. This method can be called as often as you want.
     */
    public void freeBuffers() {
        this.close();
    }

    /**
     * Frees all buffers. This method can be called as often as you want but should be called finally if you don't need the mesh producer anymore.
     */
    @Override
    public void close() {
        this.lastPass = null;
        this.lastBuffers = null;
        this.usedBuffers.values().forEach(BufferCollection::close);
        this.usedBuffers.clear();
    }
}
