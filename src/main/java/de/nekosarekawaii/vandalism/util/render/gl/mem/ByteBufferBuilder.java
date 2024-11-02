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

package de.nekosarekawaii.vandalism.util.render.gl.mem;

import de.nekosarekawaii.vandalism.util.render.gl.buffer.BufferObject;
import de.nekosarekawaii.vandalism.util.render.gl.buffer.BufferUsage;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumer;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.DataType;
import net.lenni0451.reflect.JavaBypass;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

public class ByteBufferBuilder implements AttribConsumer, AutoCloseable {

    private static final Unsafe UNSAFE = JavaBypass.getUnsafe();
    private long buffer;
    private long capacity;
    private long pos;
    
    public ByteBufferBuilder(int initialCapacity) {
        this.buffer = MemoryUtil.nmemAlloc(initialCapacity);
        this.capacity = initialCapacity;
        if (this.buffer == 0L) {
            throw new OutOfMemoryError("Failed to allocate direct memory (ByteBufferBuilder)");
        }
    }

    public ByteBufferBuilder() {
        this(524288);
    }

    private long remaining() {
        return this.capacity - this.pos;
    }

    private void ensureCapacity(long size) {
        if (this.remaining() < size) {
            final long newSize = this.capacity + Math.max(Math.max(524288, this.capacity / 2), size);
            if (newSize < this.capacity) {
                throw new IllegalStateException("ByteBufferBuffer overflow detected: " + this.capacity + " > " + newSize);
            }
            final long newBuffer = MemoryUtil.nmemRealloc(this.buffer, newSize);
            if (newBuffer == 0L) {
                throw new OutOfMemoryError("Failed to reallocate direct memory (ByteBufferBuilder)");
            }
            this.capacity = newSize;
            this.buffer = newBuffer;
        }
    }

    /**
     * Reserves the given amount of bytes in the buffer, advances the position and returns the position before the advance.
     * @param size The amount of bytes to reserve
     * @return The position before the advance
     */
    public long reserveBytes(long size) {
        this.ensureCapacity(size);
        final long pos = this.pos;
        this.pos += size;
        return pos;
    }

    public long getInternalBufferAddress() {
        return this.buffer;
    }

    public long getInternalBufferPosition() {
        return this.pos;
    }

    public long getCursorAddress() {
        return this.buffer + this.pos;
    }

    /**
     * Resets the buffer to the initial state.
     * <br>
     * You should call this method after you are done with the buffer. And want to reuse it later again.
     */
    public ByteBufferBuilder reset() {
        this.pos = 0L;
        return this;
    }

    /**
     * @return Returns a copy of the buffer that is flipped and ready to be read from. The original buffer is not flipped (You have to manually reset it if you need).
     */
    public ByteBuffer toGCByteBuffer() {
        if (this.pos > Integer.MAX_VALUE) throw new IllegalStateException("Buffer size exceeds Integer.MAX_VALUE");
        final ByteBuffer buf = BufferUtils.createByteBuffer((int) this.pos);
        MemoryUtil.memCopy(this.buffer, MemoryUtil.memAddress(buf), this.pos);
        buf.limit((int) this.pos);
        return buf;
    }

    /**
     * Uploads the content of the buffer to a new {@link BufferObject} and returns it.
     * @param usage The usage of the buffer
     * @return The new {@link BufferObject}
     */
    public BufferObject upload(BufferUsage usage) {
        final BufferObject buffer = new BufferObject();
        buffer.init(this.buffer, this.pos, usage);
        this.pos = 0L;
        return buffer;
    }

    /**
     * Uploads the content of the buffer to a new immutable {@link BufferObject} and returns it.
     * @param flags The flags to use for the buffer, see {@link org.lwjgl.opengl.GL45C#glNamedBufferStorage} for possible flags
     * @return The new {@link BufferObject}
     */
    public BufferObject uploadImmutable(int flags) {
        final BufferObject buffer = new BufferObject();
        buffer.initImmutable(this.buffer, this.pos, flags);
        this.pos = 0L;
        return buffer;
    }

    /**
     * Uploads the content of the buffer to the given {@link BufferObject}.
     * This method will call {@link BufferObject#init(ByteBuffer, BufferUsage)}.
     * So keep in mind that if the buffer is immutable, you should rather use {@link #uploadTo(long, BufferObject)}.
     * @param buffer The buffer to upload to
     * @param usage The usage of the buffer
     */
    public ByteBufferBuilder uploadTo(BufferObject buffer, BufferUsage usage) {
        buffer.init(this.buffer, this.pos, usage);
        this.pos = 0L;
        return this;
    }

    /**
     * Uploads the content of the buffer to the given {@link BufferObject}.
     * This method uses {@link BufferObject#setData(long, ByteBuffer)}. ({@link org.lwjgl.opengl.GL45C#glNamedBufferSubData})
     * @param buffer The buffer to upload to
     * @param offset The offset to start writing at
     */
    public ByteBufferBuilder uploadTo(long offset, BufferObject buffer) {
        buffer.setData(offset, this.buffer, this.pos);
        this.pos = 0L;
        return this;
    }

    /**
     * @return How many bytes have been written to the buffer
     */
    public long written() {
        return this.pos;
    }

    /**
     * @return The current capacity of the buffer
     */
    public long currentCapacity() {
        return this.capacity;
    }

    @Override
    public void close() {
        MemoryUtil.nmemFree(this.buffer);
    }

    @Override
    public AttribConsumer putBoolean(boolean b) {
        this.ensureCapacity(1);
        UNSAFE.putByte(this.getCursorAddress(), b ? (byte) 1 : (byte) 0);
        ++this.pos;
        return this;
    }

    @Override
    public AttribConsumer putByte(byte b) {
        this.ensureCapacity(1);
        UNSAFE.putByte(this.getCursorAddress(), b);
        ++this.pos;
        return this;
    }

    @Override
    public AttribConsumer putShort(short s) {
        this.ensureCapacity(2);
        UNSAFE.putShort(this.getCursorAddress(), s);
        this.pos += 2;
        return this;
    }

    @Override
    public AttribConsumer putInt(int i) {
        this.ensureCapacity(4);
        UNSAFE.putInt(this.getCursorAddress(), i);
        this.pos += 4;
        return this;
    }

    @Override
    public AttribConsumer putLong(long l) {
        this.ensureCapacity(8);
        UNSAFE.putLong(this.getCursorAddress(), l);
        this.pos += 8;
        return this;
    }

    @Override
    public AttribConsumer putFloat(float f) {
        this.ensureCapacity(4);
        UNSAFE.putFloat(this.getCursorAddress(), f);
        this.pos += 4;
        return this;
    }

    @Override
    public AttribConsumer putDouble(double d) {
        this.ensureCapacity(8);
        UNSAFE.putDouble(this.getCursorAddress(), d);
        this.pos += 8;
        return this;
    }

    @Override
    public AttribConsumer putBytes(byte... bs) {
        this.ensureCapacity(bs.length);
        UNSAFE.copyMemory(bs, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, this.getCursorAddress(), bs.length);
        this.pos += bs.length;
        return this;
    }

    @Override
    public AttribConsumer putBytes(byte[] bytes, int offset, int length) {
        this.ensureCapacity(length);
        UNSAFE.copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET + offset, null, this.getCursorAddress(), length);
        this.pos += length;
        return this;
    }

    @Override
    public AttribConsumer putBytes(ByteBuffer buffer) {
        this.ensureCapacity(buffer.remaining());
        MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), this.getCursorAddress(), buffer.remaining());
        this.pos += buffer.remaining();
        buffer.position(buffer.limit());
        return this;
    }

    @Override
    public AttribConsumer pos(float x, float y, float z) {
        this.ensureCapacity(12);
        UNSAFE.putFloat(this.getCursorAddress(), x);
        UNSAFE.putFloat(this.getCursorAddress() + 4L, y);
        UNSAFE.putFloat(this.getCursorAddress() + 8L, z);
        this.pos += 12;
        return this;
    }

    @Override
    public AttribConsumer halfPos(float x, float y, float z) {
        this.ensureCapacity(6);
        UNSAFE.putShort(this.getCursorAddress(), (short) DataType.floatToHalfBits(x));
        UNSAFE.putShort(this.getCursorAddress() + 2L, (short) DataType.floatToHalfBits(y));
        UNSAFE.putShort(this.getCursorAddress() + 4L, (short) DataType.floatToHalfBits(z));
        this.pos += 6;
        return this;
    }

    /**
     * This method does <strong>nothing</strong>
     */
    @Override
    public void next() {
    }

    /**
     * This method does <strong>nothing</strong>
     */
    @Override
    public void nextConnectedPrimitive() {
    }
}
