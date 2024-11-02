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
import de.nekosarekawaii.vandalism.util.render.gl.render.passes.RenderPass;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.gl.utils.TemporaryValues;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VAO;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL45C;

import java.util.List;

@Log4j2
public class PersistentMesh implements AutoCloseable {

    private final Object2ObjectMap<RenderPass, PassEntry> entryByRenderPass;
    private final List<PassEntry> passes;

    PersistentMesh(List<PassEntry> passes) {
        this.entryByRenderPass = new Object2ObjectOpenHashMap<>();
        this.passes = passes;
        for (PassEntry pass : this.passes) {
            this.entryByRenderPass.put(pass.pass(), pass);
        }
    }

    public void draw(Matrix4fc transform) {
        for (PassEntry pass : this.passes) {
            this.drawPassInternal(pass, transform, true);
        }
    }

    public void draw() {
        this.draw(TemporaryValues.IDENTITY_MATRIX4F);
    }

    public void drawWithoutShader(Matrix4fc transform) {
        for (PassEntry pass : this.passes) {
            this.drawPassInternal(pass, transform, false);
        }
    }

    public void drawWithoutShader() {
        this.drawWithoutShader(TemporaryValues.IDENTITY_MATRIX4F);
    }

    public boolean drawPass(RenderPass pass, Matrix4fc transform) {
        final PassEntry entry = this.entryByRenderPass.get(pass);
        if (entry == null) return false;
        this.drawPassInternal(entry, transform, true);
        return true;
    }

    public boolean drawPass(RenderPass pass) {
        return this.drawPass(pass, TemporaryValues.IDENTITY_MATRIX4F);
    }

    public boolean drawPassWithoutShader(RenderPass pass, Matrix4fc transform) {
        final PassEntry entry = this.entryByRenderPass.get(pass);
        if (entry == null) return false;
        this.drawPassInternal(entry, transform, false);
        return true;
    }

    public boolean drawPassWithoutShader(RenderPass pass) {
        return this.drawPassWithoutShader(pass, TemporaryValues.IDENTITY_MATRIX4F);
    }

    private void drawPassInternal(PassEntry entry, Matrix4fc transform, boolean withShaders) {
        final int mode = entry.pass.getPrimitiveType().getGlType();

        final int prevVaoId = VAO.currentBoundId();
        entry.vao.bind();
        entry.pass.setupState();
        ShaderProgram shader = null;
        if (withShaders) {
            shader = entry.instanceCount == -1 ? entry.pass.getShaderProgram() : entry.pass.getInstancedShaderProgram();
            if (shader == null) log.warn("No shader program for pass {}", entry.pass);
            else {
                shader.bind();
                GlobalUniforms.setGlobalUniforms(shader, false);
                entry.pass.setupUniforms(shader, entry.instanceCount);
                GlobalUniforms.setTransformMatrix(shader, transform);
            }
        }

        GL45C.glBindBuffer(GL45C.GL_DRAW_INDIRECT_BUFFER, entry.commandBuffer.id());
        final long indirectDataOffset = entry.indirectDataOffset;
        final int multiDrawCount = entry.multiDrawCount;
        if (entry.drawElements) {
            GL45C.glMultiDrawElementsIndirect(mode, entry.indexType.getGlType(), indirectDataOffset, multiDrawCount == -1 ? 1 : multiDrawCount, 0);
        } else {
            GL45C.glMultiDrawArraysIndirect(mode, indirectDataOffset, multiDrawCount == -1 ? 1 : multiDrawCount, 0);
        }

        if (shader != null) shader.unbind();
        entry.pass.cleanupState();
        VAO.bind(prevVaoId);
    }

    @Override
    public void close() {
        for (PassEntry pass : this.passes) {
            if (pass.elementBuffer != null && pass.ownedElementBuffer) pass.elementBuffer.close();
            boolean closeCommandBuf = true;
            for (BufferObject bufferObject : pass.bufferObjects) {
                if (bufferObject == pass.commandBuffer) closeCommandBuf = false;
                bufferObject.close();
            }
            if (closeCommandBuf) pass.commandBuffer.close();
            pass.vao.close();
        }
        this.passes.clear();
    }

    record PassEntry(RenderPass pass, VAO vao, BufferObject elementBuffer, boolean ownedElementBuffer, IndexType indexType,
                     BufferObject[] bufferObjects, BufferObject commandBuffer, long indirectDataOffset,
                     boolean drawElements, int multiDrawCount, int instanceCount) {}
}
