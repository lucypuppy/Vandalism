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

package de.nekosarekawaii.vandalism.util.render.gl.vertex;

import de.nekosarekawaii.vandalism.util.render.gl.buffer.BufferObject;
import de.nekosarekawaii.vandalism.util.render.gl.buffer.BufferTarget;
import it.unimi.dsi.fastutil.ints.IntCollection;
import org.lwjgl.opengl.GL45C;

import java.util.Collection;
import java.util.OptionalInt;

public class VAO implements AutoCloseable {

    private final int id;

    private VAO(int id) {
        this.id = id;
    }

    /**
     * Creates a new VAO.
     */
    public VAO() {
        this(GL45C.glCreateVertexArrays());
    }

    public int id() {
        return this.id;
    }

    public BufferObject getElementBuffer() {
        return BufferObject.byId(GL45C.glGetVertexArrayi(this.id, GL45C.GL_ELEMENT_ARRAY_BUFFER_BINDING));
    }

    /**
     * @param buffer The buffer to bind as the element buffer, if null then 0 will be bound.
     */
    public void setElementBuffer(BufferObject buffer) {
        GL45C.glVertexArrayElementBuffer(this.id, buffer == null ? 0 : buffer.id());
    }

    /**
     * @param bindingIndex An index where the vertex buffer and the specified layout will be bound to.
     * @param buffer The vertex buffer to bind.
     * @param offset The offset in the vertex buffer.
     * @param layout The data layout of the vertex buffer.
     */
    public void bindVertexBufferAndLayout(int bindingIndex, BufferObject buffer, long offset, VertexLayout layout) {
        GL45C.glVertexArrayVertexBuffer(this.id, bindingIndex, buffer.id(), offset, layout.stride()); // set vertex buffer at binding index
        for (VertexLayoutElement element : layout.elements()) {
            GL45C.glEnableVertexArrayAttrib(this.id, element.attribIndex()); // Enable the attribute at attribIndex
            if (element.isInteger()) {
                GL45C.glVertexArrayAttribIFormat(this.id, element.attribIndex(), element.count(), // Specify the layout of the double-precision element at attribIndex
                        element.dataType().getGlType(), element.offset());
            } else if (element.dataType().isDoublePrecision()) {
                GL45C.glVertexArrayAttribLFormat(this.id, element.attribIndex(), element.count(), // Specify the layout of the double-precision element at attribIndex
                        element.dataType().getGlType(), element.offset());
            } else {
                GL45C.glVertexArrayAttribFormat(this.id, element.attribIndex(), element.count(), // Specify the layout of the element at attribIndex
                        element.dataType().getGlType(), element.isNormalized(), element.offset());
            }
            GL45C.glVertexArrayAttribBinding(this.id, element.attribIndex(), bindingIndex); // Bind the element at attribIndex to the buffer at bindingIndex
        }
        GL45C.glVertexArrayBindingDivisor(this.id, bindingIndex, layout.divisor());
    }

    /**
     * Appends a vertex buffer and its layout to this VAO using non-DSA functions.
     * <br>
     * This method also stores the active state and restores it after the operation. So you don't have to worry about the states.
     * @param buffer The vertex buffer to bind.
     * @param offset The offset in the vertex buffer.
     * @param layout The data layout of the vertex buffer.
     */
    public void appendVertexBufferAndLayout(BufferObject buffer, long offset, VertexLayout layout) {
        final int activeVaoId = currentBoundId();
        final int activeArrayBufferId = GL45C.glGetInteger(GL45C.GL_ARRAY_BUFFER_BINDING);
        this.bind();
        buffer.bind(BufferTarget.ARRAY_BUFFER);

        for (VertexLayoutElement element : layout.elements()) {
            GL45C.glEnableVertexAttribArray(element.attribIndex()); // Enable the attribute at attribIndex
            if (element.isInteger()) {
                GL45C.glVertexAttribIPointer(element.attribIndex(), element.count(), // Specify the layout of the double-precision element at attribIndex
                        element.dataType().getGlType(), layout.stride(), offset + element.offset());
            } else if (element.dataType().isDoublePrecision()) {
                GL45C.glVertexAttribLPointer(element.attribIndex(), element.count(), // Specify the layout of the double-precision element at attribIndex
                        element.dataType().getGlType(), layout.stride(), offset + element.offset());
            } else {
                GL45C.glVertexAttribPointer(element.attribIndex(), element.count(), // Specify the layout of the element at attribIndex
                        element.dataType().getGlType(), element.isNormalized(), layout.stride(), offset + element.offset());
            }
            if (layout.divisor() > 0) {
                GL45C.glVertexAttribDivisor(element.attribIndex(), layout.divisor());
            }
        }

        GL45C.glBindVertexArray(activeVaoId);
        GL45C.glBindBuffer(GL45C.GL_ARRAY_BUFFER, activeArrayBufferId);
    }

    /**
     * Enables the attribute at the specified index.
     * @param attribIndex The index of the attribute to enable.
     */
    public void enableAttrib(int attribIndex) {
        GL45C.glEnableVertexArrayAttrib(this.id, attribIndex);
    }

    /**
     * Binds a vertex buffer to the specified binding index with the specified offset and stride.
     * @param bindingIndex The index where the vertex buffer will be bound to.
     * @param buffer The vertex buffer to bind.
     * @param offset The base offset in the vertex buffer.
     * @param stride The stride of the vertex buffer. (The advance between two elements)
     */
    public void bindVertexBuffer(int bindingIndex, BufferObject buffer, long offset, int stride) {
        GL45C.glVertexArrayVertexBuffer(this.id, bindingIndex, buffer.id(), offset, stride);
    }

    /**
     * Bind the specified attribute index to the specified binding index.
     * <br>
     * This method creates a relationship between the attribute index and the binding index.
     * This means that the attribute will get its data from the buffer bound to the specified binding index.
     * @param attribIndex The index of the attribute.
     * @param bindingIndex The index of the binding.
     */
    public void bindAttribToBuffer(int attribIndex, int bindingIndex) {
        GL45C.glVertexArrayAttribBinding(this.id, attribIndex, bindingIndex);
    }

    /**
     * Sets the divisor of the specified binding index.
     * @param bindingIndex The index of the binding.
     * @param divisor The divisor to set. (0 = attribute is per-vertex, 1 = attribute is per-instance, 2 = attribute is per-2-instances, ...)
     */
    public void setBindingDivisor(int bindingIndex, int divisor) {
        GL45C.glVertexArrayBindingDivisor(this.id, bindingIndex, divisor);
    }

    public void setAttribFormat(int attribIndex, int size, int type, boolean normalized, int offset) {
        GL45C.glVertexArrayAttribFormat(this.id, attribIndex, size, type, normalized, offset);
    }

    public void setAttribIFormat(int attribIndex, int size, int type, int offset) {
        GL45C.glVertexArrayAttribIFormat(this.id, attribIndex, size, type, offset);
    }

    public void setAttribLFormat(int attribIndex, int size, int type, int offset) {
        GL45C.glVertexArrayAttribLFormat(this.id, attribIndex, size, type, offset);
    }

    /**
     * Disables the attribute at the specified index.
     * @param attribIndex The index of the attribute to disable.
     */
    public void disableAttrib(int attribIndex) {
        GL45C.glDisableVertexArrayAttrib(this.id, attribIndex);
    }

    /**
     * @return The number of attributes that were disabled.
     */
    public int disableAllAttribs() {
        final int maxAttribs = getMaxVertexAttribs();
        int count = 0;
        for (int i = 0; i < maxAttribs; i++) {
            if (this.isAttribEnabled(i)) {
                this.disableAttrib(i);
                ++count;
            }
        }
        return count;
    }

    /**
     * @return The number of attributes that are currently enabled.
     */
    public int getEnabledAttribCount() {
        final int maxAttribs = getMaxVertexAttribs();
        int count = 0;
        for (int i = 0; i < maxAttribs; i++) {
            if (this.isAttribEnabled(i)) {
                ++count;
            }
        }
        return count;
    }

    /**
     * @param outAttribIndices The collection to which the indices of the enabled attributes will be added.
     * @return The number of attributes that are currently enabled.
     */
    public int getEnabledAttribs(IntCollection outAttribIndices) {
        final int maxAttribs = getMaxVertexAttribs();
        int count = 0;
        for (int i = 0; i < maxAttribs; i++) {
            if (this.isAttribEnabled(i)) {
                outAttribIndices.add(i);
                ++count;
            }
        }
        return count;
    }

    /**
     * @param outAttribIndices The collection to which the indices of the enabled attributes will be added.
     * @return The number of attributes that are currently enabled.
     */
    public int getEnabledAttribs(Collection<Integer> outAttribIndices) {
        final int maxAttribs = getMaxVertexAttribs();
        int count = 0;
        for (int i = 0; i < maxAttribs; i++) {
            if (this.isAttribEnabled(i)) {
                outAttribIndices.add(i);
                ++count;
            }
        }
        return count;
    }

    /**
     * @param attribIndex The index of the attribute to check.
     * @return Whether the attribute at the specified index is enabled.
     */
    public boolean isAttribEnabled(int attribIndex) {
        return GL45C.glGetVertexArrayIndexedi(this.id, attribIndex, GL45C.GL_VERTEX_ATTRIB_ARRAY_ENABLED) != 0;
    }

    /**
     * @return The first free attribute index or an empty optional if there is no free index.
     */
    public OptionalInt getFreeAttribIndex() {
        final int maxAttribs = getMaxVertexAttribs();
        for (int i = 0; i < maxAttribs; i++) {
            if (!this.isAttribEnabled(i)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public OptionalInt getFreeAttribIndices(int count) {
        final int maxAttribs = getMaxVertexAttribs();
        for (int i = 0; i < maxAttribs; i++) {
            if (this.isAttribEnabled(i)) continue;
            boolean free = true;
            for (int j = 1; j < count; j++) {
                if (this.isAttribEnabled(i + j)) {
                    free = false;
                    break;
                }
            }
            if (free) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Binds this VAO to the current context.
     */
    public void bind() {
        GL45C.glBindVertexArray(this.id);
    }

    /**
     * Bind the VAO with the ID 0.
     */
    public void unbind() {
        GL45C.glBindVertexArray(0);
    }

    /**
     * Draws the currently bound VAO.
     * <br>
     * <strong>NOTE:</strong> This method does <strong>NOT</strong> bind the before drawing.
     * @param mode the drawing mode, e.g. GL_TRIANGLES
     * @param first the index of the first vertex to draw
     * @param count the number of vertices to draw
     */
    public void drawArrays(int mode, int first, int count) {
        GL45C.glDrawArrays(mode, first, count);
    }

    /**
     * Draws the currently bound VAO multiple times.
     * <br>
     * <strong>NOTE:</strong> This method does <strong>NOT</strong> bind the before drawing.
     * @param mode the drawing mode, e.g. GL_TRIANGLES
     * @param first the index of the first vertex to draw
     * @param count the number of vertices to draw
     * @param instanceCount the number of instances to draw
     */
    public void drawArraysInstanced(int mode, int first, int count, int instanceCount) {
        GL45C.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    /**
     * Draws the currently bound VAO using the element buffer assigned to the current VAO.
     * <br>
     * <strong>NOTE:</strong> This method does <strong>NOT</strong> bind the before drawing.
     * @param mode the drawing mode, e.g. GL_TRIANGLES
     * @param count the number of vertices to draw
     * @param type the type of the indices in the element buffer, e.g. GL_UNSIGNED_INT
     * @param offset the offset in the element buffer or the first index
     */
    public void drawElements(int mode, int count, int type, long offset) {
        GL45C.glDrawElements(mode, count, type, offset);
    }

    /**
     * Draws the currently bound VAO multiple times using the element buffer assigned to the current VAO.
     * <br>
     * <strong>NOTE:</strong> This method does <strong>NOT</strong> bind the before drawing.
     * @param mode the drawing mode, e.g. GL_TRIANGLES
     * @param count the number of vertices to draw
     * @param type the type of the indices in the element buffer, e.g. GL_UNSIGNED_INT
     * @param offset the offset in the element buffer or the first index
     * @param instanceCount the number of instances to draw
     */
    public void drawElementsInstanced(int mode, int count, int type, long offset, int instanceCount) {
        GL45C.glDrawElementsInstanced(mode, count, type, offset, instanceCount);
    }

    public void multiDrawArrays(int mode, int[] first, int[] count) {
        GL45C.glMultiDrawArrays(mode, first, count);
    }

    /**
     * Deletes this VAO.
     */
    @Override
    public void close() {
        GL45C.glDeleteVertexArrays(this.id);
    }

    /** Returns the VAO with the given ID. */
    public static VAO byId(int id) {
        return new VAO(id);
    }

    /**
     * @return The VAO-ID that is currently bound in the current context.
     */
    public static int currentBoundId() {
        return GL45C.glGetInteger(GL45C.GL_VERTEX_ARRAY_BINDING);
    }

    /**
     * @return The VAO that is currently bound in the current context.
     */
    public static VAO currentBound() {
        return byId(currentBoundId());
    }

    /**
     * @return The maximum number of vertex attributes that can be used.
     */
    public static int getMaxVertexAttribs() {
        return GL45C.glGetInteger(GL45C.GL_MAX_VERTEX_ATTRIBS);
    }

    /**
     * @return The maximum number of vertex attribute bindings that can be used.
     */
    public static int getMaxVertexAttribBindings() {
        return GL45C.glGetInteger(GL45C.GL_MAX_VERTEX_ATTRIB_BINDINGS);
    }

    public static void bind(int vaoId) {
        GL45C.glBindVertexArray(vaoId);
    }
}
