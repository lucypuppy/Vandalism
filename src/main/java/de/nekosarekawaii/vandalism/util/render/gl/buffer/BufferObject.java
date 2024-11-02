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

package de.nekosarekawaii.vandalism.util.render.gl.buffer;

import lombok.extern.log4j.Log4j2;
import org.lwjgl.opengl.GL45C;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

@Log4j2
public class BufferObject implements AutoCloseable {

    private final int id;

    private BufferObject(int id) {
        this.id = id;
    }

    public BufferObject() {
        this(GL45C.glCreateBuffers());
    }

    public void init(long bufferSize, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, bufferSize, usage.getGlType());
    }

    public void init(long buffer, long bufferSize, BufferUsage usage) {
        GL45C.nglNamedBufferData(this.id, bufferSize, buffer, usage.getGlType());
    }

    public void init(ByteBuffer buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(ShortBuffer buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(IntBuffer buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(LongBuffer buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(FloatBuffer buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(DoubleBuffer buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(short[] buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(int[] buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(long[] buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(float[] buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void init(double[] buffer, BufferUsage usage) {
        GL45C.glNamedBufferData(this.id, buffer, usage.getGlType());
    }

    public void initImmutable(long bufferSize, int flags) {
        GL45C.glNamedBufferStorage(this.id, bufferSize, flags);
    }

    public void initImmutable(long buffer, long bufferSize, int flags) {
        GL45C.nglNamedBufferStorage(this.id, bufferSize, buffer, flags);
    }

    public void initImmutable(ByteBuffer buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(ShortBuffer buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(IntBuffer buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(FloatBuffer buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(DoubleBuffer buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(short[] buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(int[] buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(float[] buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void initImmutable(double[] buffer, int flags) {
        GL45C.glNamedBufferStorage(this.id, buffer, flags);
    }

    public void setData(long offset, long buffer, long bufferSize) {
        GL45C.nglNamedBufferSubData(this.id, offset, bufferSize, buffer);
    }

    public void setData(long offset, ByteBuffer buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, ShortBuffer buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, IntBuffer buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, LongBuffer buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, FloatBuffer buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, DoubleBuffer buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, short[] buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, int[] buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, long[] buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, float[] buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void setData(long offset, double[] buffer) {
        GL45C.glNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, ByteBuffer buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, ShortBuffer buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, IntBuffer buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, LongBuffer buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, FloatBuffer buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, DoubleBuffer buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, short[] buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, int[] buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, long[] buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, float[] buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public void getData(long offset, double[] buffer) {
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
    }

    public byte[] getData(long offset, int length) {
        final ByteBuffer buffer = ByteBuffer.allocate(length).order(ByteOrder.nativeOrder());
        GL45C.glGetNamedBufferSubData(this.id, offset, buffer);
        return buffer.array();
    }

    public byte[] getData() {
        return this.getData(0, (int) this.size());
    }

    public MappedBuffer map(BufferAccess access, long size) {
        final ByteBuffer buffer = GL45C.glMapNamedBuffer(this.id, access.getGlType(), size, null);
        return buffer == null ? null : new MappedBuffer(this, buffer);
    }

    public MappedBuffer map(BufferAccess access) {
        return this.map(access, this.size());
    }

    public boolean unmap() {
        return GL45C.glUnmapNamedBuffer(this.id);
    }

    public void bind(BufferTarget target) {
        GL45C.glBindBuffer(target.getGlType(), this.id);
    }

    public void bindBase(BufferTarget target, int index) {
        GL45C.glBindBufferBase(target.getGlType(), index, this.id);
    }

    public void bindRange(BufferTarget target, int index, long offset, long size) {
        GL45C.glBindBufferRange(target.getGlType(), index, this.id, offset, size);
    }

    public int id() {
        return this.id;
    }

    public long size() {
        return GL45C.glGetNamedBufferParameteri64(this.id, GL45C.GL_BUFFER_SIZE);
    }

    public boolean isMapped() {
        return GL45C.glGetNamedBufferParameteri(this.id, GL45C.GL_BUFFER_MAPPED) != GL45C.GL_FALSE;
    }

    public boolean isImmutable() {
        return GL45C.glGetNamedBufferParameteri(this.id, GL45C.GL_BUFFER_IMMUTABLE_STORAGE) != GL45C.GL_FALSE;
    }

    public BufferUsage usage() {
        final int glUsage = GL45C.glGetNamedBufferParameteri(id, GL45C.GL_BUFFER_USAGE);
        BufferUsage usage = BufferUsage.byGlType(glUsage);
        if (usage == null) {
            log.warn("Found unknown/unhandled GL buffer usage while getting GL buffer usage: {}; Using STATIC_DRAW as fallback usage.", glUsage);
            usage = BufferUsage.STATIC_DRAW;
        }
        return usage;
    }

    /**
     * Deletes this buffer object.
     */
    @Override
    public void close() {
        GL45C.glDeleteBuffers(this.id);
    }

    public static BufferObject byId(int id) {
        return new BufferObject(id);
    }
}
