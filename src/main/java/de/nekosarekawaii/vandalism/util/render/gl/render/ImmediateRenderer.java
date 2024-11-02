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

import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.gl.buffer.BufferObject;
import de.nekosarekawaii.vandalism.util.render.gl.mem.BufferPool;
import de.nekosarekawaii.vandalism.util.render.gl.mem.ByteBufferBuilder;
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.RenderPass;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.gl.utils.TemporaryValues;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VAO;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayout;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayoutElement;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lombok.extern.log4j.Log4j2;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL45C;

import java.util.Iterator;
import java.util.Map;

@Log4j2
public class ImmediateRenderer implements AttribConsumerProvider, AutoCloseable {

    private final Object2ObjectMap<RenderPass, BufferCollection> usedBuffers = new Object2ObjectLinkedOpenHashMap<>();
    private final BufferPool bufferPool;
    private RenderPass lastPass;
    private BufferCollection lastBuffers;

    public ImmediateRenderer(BufferPool bufferPool) {
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

    public boolean isRenderPassUsed(RenderPass pass) {
        return this.usedBuffers.containsKey(pass);
    }

    public boolean isActive() {
        return !this.usedBuffers.isEmpty();
    }

    public boolean isIdle() {
        return this.usedBuffers.isEmpty();
    }

    /**
     * Draws all passes. The buffers will be reset after the draw call.
     */
    public void draw(Matrix4fc transform) {
        this.drawInternal(transform, true);
    }

    /**
     * Draws all passes. The buffers will be reset after the draw call.
     */
    public void draw() {
        this.draw(TemporaryValues.IDENTITY_MATRIX4F);
    }

    /**
     * Draws all passes without a shader, so you need to bind and set up your own shader. The buffers will be reset after the draw call.
     */
    public void drawWithoutShader(Matrix4fc transform) {
        this.drawInternal(transform, false);
    }

    /**
     * Draws all passes without a shader, so you need to bind and set up your own shader. The buffers will be reset after the draw call.
     */
    public void drawWithoutShader() {
        this.drawWithoutShader(TemporaryValues.IDENTITY_MATRIX4F);
    }

    private void drawInternal(Matrix4fc transform, boolean withShaders) {
        final ObjectSet<Map.Entry<RenderPass, BufferCollection>> entries = this.usedBuffers.entrySet();
        final Iterator<Map.Entry<RenderPass, BufferCollection>> iter = entries.iterator();
        this.lastPass = null;
        this.lastBuffers = null;
        while (iter.hasNext()) {
            final Map.Entry<RenderPass, BufferCollection> entry = iter.next();
            final RenderPass pass = entry.getKey();
            final BufferCollection buffers = entry.getValue();
            iter.remove();
            this.drawPassInternal(pass, buffers, transform, withShaders);
            buffers.reset();
        }
        this.reset();
    }

    /**
     * Draws the given pass. The buffers will be reset after the draw call.
     * @param pass The pass to draw.
     * @param transform The transform matrix to use.
     */
    public void drawPass(RenderPass pass, Matrix4fc transform) {
        final BufferCollection buffers = this.usedBuffers.remove(pass);
        if (buffers == null) return;
        if (this.lastPass == pass) {
            this.lastPass = null;
            this.lastBuffers = null;
        }
        this.drawPassInternal(pass, buffers, transform, true);
        buffers.reset();
    }

    /**
     * Draws the given pass. The buffers will be reset after the draw call.
     * @param pass The pass to draw.
     */
    public void drawPass(RenderPass pass) {
        this.drawPass(pass, TemporaryValues.IDENTITY_MATRIX4F);
    }

    /**
     * Draws the given pass without a shader, so you need to bind and set up your own shader. The buffers will be reset after the draw call.
     * @param pass The pass to draw.
     * @param transform The transform matrix to use.
     */
    public void drawPassWithoutShader(RenderPass pass, Matrix4fc transform) {
        final BufferCollection buffers = this.usedBuffers.remove(pass);
        if (buffers == null) return;
        if (this.lastPass == pass) {
            this.lastPass = null;
            this.lastBuffers = null;
        }
        this.drawPassInternal(pass, buffers, transform, false);
        buffers.reset();
    }

    /**
     * Draws the given pass without a shader, so you need to bind and set up your own shader. The buffers will be reset after the draw call.
     * @param pass The pass to draw.
     */
    public void drawPassWithoutShader(RenderPass pass) {
        this.drawPassWithoutShader(pass, TemporaryValues.IDENTITY_MATRIX4F);
    }

    private void drawPassInternal(RenderPass pass, BufferCollection buffers, Matrix4fc transform, boolean withShaders) {
        final VertexLayout vertexLayout = pass.getVertexLayout();
        final VertexLayout instanceLayout = pass.getInstanceLayout();
        boolean instanced = false;

        final VAO vao = Buffers.getVertexArrayPool().borrowVAO(vertexLayout);
        try {
            int bufferCount = 1;
            bufferCount += buffers.getCustomBuffers().size();
            final BufferObject[] bufferObjects = new BufferObject[bufferCount];
            BufferObject elementBuffer = null;
            final int mode = pass.getPrimitiveType().getGlType();
            final BufferAttribConsumer mainBuffer = buffers.getMainBuffer();
            int vertexCount = buffers.getMainBuffer().getVertexNumNextCalls();
            int indexCount = 0;
            IndexType indexType = null;
            instanced = instanceLayout != null && mainBuffer.isInstanced();
            final long instanceDataOffset = mainBuffer.getInstanceDataOffset();
            final int instanceCount = instanced ? mainBuffer.getInstanceCount() : 1;

            final boolean connectedPrimitives = pass.getPrimitiveType().isConnectedPrimitive();
            final int multiDrawCount = connectedPrimitives ? mainBuffer.getMultiDrawCount() : -1;
            final int[] multiDrawOffsets, multiDrawCounts;
            if (multiDrawCount != -1) {
                multiDrawOffsets = new int[multiDrawCount];
                multiDrawCounts = new int[multiDrawCount];
                mainBuffer.getMultiDrawOffsetsAndCounts(multiDrawOffsets, multiDrawCounts);
            } else multiDrawCounts = multiDrawOffsets = null;

            try {
                final long indirectDataOffset;

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
                    elementBuffer = null; // Don't delete the element buffer, it's a global buffer
                    hasElementBuffer = true;
                    vertexCount /= 4;
                    vertexCount *= 6;
                    indexCount = vertexCount;
                }

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

                    final BufferObject mainBuf = buf.uploadImmutable(0);
                    vao.bindVertexBufferAndLayout(0, mainBuf, 0L, vertexLayout);
                    bufferObjects[0] = mainBuf;

                }
                int bindingIndex = 1;
                for (Map.Entry<VertexLayout, BufferAttribConsumer> entry : buffers.getCustomBuffers().entrySet()) {
                    final BufferObject buffer = entry.getValue().getBuffer().uploadImmutable(0);
                    vao.bindVertexBufferAndLayout(bindingIndex, buffer, 0L, entry.getKey());
                    bufferObjects[bindingIndex] = buffer;
                    ++bindingIndex;
                }
                if (instanced) {
                    vao.bindVertexBufferAndLayout(bindingIndex, bufferObjects[0], instanceDataOffset, instanceLayout);
                }

                final int prevVaoId = VAO.currentBoundId();
                vao.bind();
                pass.setupState();
                ShaderProgram shader = null;
                if (withShaders) {
                    shader = instanced ? pass.getInstancedShaderProgram() : pass.getShaderProgram();
                    if (shader == null) log.warn("No shader program for pass {}", pass);
                    else {
                        shader.bind();
                        GlobalUniforms.setGlobalUniforms(shader, false);
                        pass.setupUniforms(shader, mainBuffer.getInstanceCount()); // use mainBuffer.getInstanceCount() instead of instanceCount because the instance count can be -1 for non-instanced rendering
                        GlobalUniforms.setTransformMatrix(shader, transform);
                    }
                }

                GL45C.glBindBuffer(GL45C.GL_DRAW_INDIRECT_BUFFER, bufferObjects[0].id());
                if (hasElementBuffer) {
                    GL45C.glMultiDrawElementsIndirect(mode, indexType.getGlType(), indirectDataOffset, multiDrawCount == -1 ? 1 : multiDrawCount, 0);
                } else {
                    GL45C.glMultiDrawArraysIndirect(mode, indirectDataOffset, multiDrawCount == -1 ? 1 : multiDrawCount, 0);
                }

                if (shader != null) shader.unbind();
                pass.cleanupState();
                VAO.bind(prevVaoId);
            } finally {
                if (elementBuffer != null) elementBuffer.close();
                for (BufferObject buffer : bufferObjects) {
                    buffer.close();
                }
            }
        } finally {
            if (instanced) {
                for (VertexLayoutElement element : instanceLayout.elements()) {
                    vao.disableAttrib(element.attribIndex());
                }
            }
            Buffers.getVertexArrayPool().returnVAO(vao);
        }
    }

    /**
     * Resets the renderer. All buffers will be reset and be ready for reuse.
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
     * Resets the buffers for the given pass. The buffers will be ready for reuse.
     * @param pass The pass to reset.
     */
    public void reset(RenderPass pass) {
        final BufferCollection buffers = this.usedBuffers.remove(pass);
        if (buffers == null) return;
        if (this.lastPass == pass) {
            this.lastPass = null;
            this.lastBuffers = null;
        }
        buffers.reset();
    }

    /**
     * Frees all buffers. This method can be called as often as you want.
     */
    public void freeBuffers() {
        this.close();
    }

    /**
     * Frees all buffers. This method can be called as often as you want but should be called finally if you don't need the renderer anymore.
     */
    @Override
    public void close() {
        this.lastPass = null;
        this.lastBuffers = null;
        this.usedBuffers.values().forEach(BufferCollection::close);
        this.usedBuffers.clear();
    }
}
