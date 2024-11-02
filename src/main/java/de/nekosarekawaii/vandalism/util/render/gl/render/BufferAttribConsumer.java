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

import de.nekosarekawaii.vandalism.util.render.gl.mem.ByteBufferBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@RequiredArgsConstructor
public class BufferAttribConsumer implements InstancedAttribConsumer {

    @Getter(AccessLevel.NONE)
    private int numNextCalls;
    private IntList connectedPrimitivesIndices;
    private final ByteBufferBuilder buffer;
    private int vertexNumNextCalls;
    private long instanceDataOffset = -1;
    private int instanceCount = -1;

    @Override
    public AttribConsumer putBoolean(boolean b) {
        this.buffer.putBoolean(b);
        return this;
    }

    @Override
    public AttribConsumer putByte(byte b) {
        this.buffer.putByte(b);
        return this;
    }

    @Override
    public AttribConsumer putShort(short s) {
        this.buffer.putShort(s);
        return this;
    }

    @Override
    public AttribConsumer putInt(int i) {
        this.buffer.putInt(i);
        return this;
    }

    @Override
    public AttribConsumer putLong(long l) {
        this.buffer.putLong(l);
        return this;
    }

    @Override
    public AttribConsumer putFloat(float f) {
        this.buffer.putFloat(f);
        return this;
    }

    @Override
    public AttribConsumer putDouble(double d) {
        this.buffer.putDouble(d);
        return this;
    }

    @Override
    public AttribConsumer putBytes(byte... bs) {
        this.buffer.putBytes(bs);
        return this;
    }

    @Override
    public AttribConsumer putBytes(byte[] bytes, int offset, int length) {
        this.buffer.putBytes(bytes, offset, length);
        return this;
    }

    @Override
    public AttribConsumer putBytes(ByteBuffer buffer) {
        this.buffer.putBytes(buffer);
        return this;
    }

    @Override
    public AttribConsumer pos(float x, float y, float z) {
        this.buffer.pos(x, y, z);
        return this;
    }

    @Override
    public AttribConsumer halfPos(float x, float y, float z) {
        this.buffer.halfPos(x, y, z);
        return this;
    }

    @Override
    public void next() {
        if (this.instanceDataOffset == -1) ++this.vertexNumNextCalls;
        ++this.numNextCalls;
        this.buffer.next();
    }

    @Override
    public void nextConnectedPrimitive() {
        if (this.instanceDataOffset != -1) throw new IllegalStateException("nextConnectedPrimitive() must not be called after beginInstance()");
        if (this.numNextCalls == 0) return;
        if (this.connectedPrimitivesIndices == null || this.connectedPrimitivesIndices.isEmpty()) {
            if (this.connectedPrimitivesIndices == null) this.connectedPrimitivesIndices = new IntArrayList();
            this.connectedPrimitivesIndices.add(0);
            this.connectedPrimitivesIndices.add(this.numNextCalls);
        } else {
            final int lastIndex = this.connectedPrimitivesIndices.size() - 1;
            final int lastPrimitiveOffset = this.connectedPrimitivesIndices.getInt(lastIndex);
            if (lastPrimitiveOffset == this.numNextCalls) {
                //throw new IllegalStateException("nextConnectedPrimitive() must not be called twice in the same execution of next()");
            } else {
                this.connectedPrimitivesIndices.add(this.numNextCalls);
            }
        }
        this.buffer.nextConnectedPrimitive();
    }

    @Override
    public InstancedAttribConsumer beginInstance() {
        if (this.instanceDataOffset == -1) {
            this.instanceDataOffset = this.buffer.written();
            this.instanceCount = 1;
        } else {
            ++this.instanceCount;
        }
        this.numNextCalls = 0;
        return this;
    }

    public void reset() {
        this.numNextCalls = 0;
        this.vertexNumNextCalls = 0;
        this.instanceDataOffset = -1;
        this.instanceCount = -1;
        if (this.connectedPrimitivesIndices != null) this.connectedPrimitivesIndices.clear();
        this.buffer.reset();
    }

    public boolean isInstanced() {
        return this.instanceDataOffset != -1;
    }

    public int getMultiDrawCount() {
        if (this.connectedPrimitivesIndices == null || this.connectedPrimitivesIndices.isEmpty()) return -1;
        final int lastEntryIndex = this.connectedPrimitivesIndices.size() - 1;
        final long lastPrimitiveOffset = this.connectedPrimitivesIndices.getInt(lastEntryIndex);
        final boolean separatorAtTail = lastPrimitiveOffset == this.numNextCalls;
        return this.connectedPrimitivesIndices.size() - (separatorAtTail ? 1 : 0);
    }

    public void getMultiDrawOffsetsAndCounts(int[] offsets, int[] counts) {
        final int multiDrawCount = this.getMultiDrawCount();
        if (multiDrawCount == -1) throw new IllegalStateException("No multi draw calls available");
        if (offsets.length < multiDrawCount) throw new IllegalArgumentException("offsets array is too small");
        if (counts.length < multiDrawCount) throw new IllegalArgumentException("counts array is too small");
        int prevOffset = 0;
        offsets[0] = 0;
        for (int i = 1; i < offsets.length; i++) {
            final int offset = this.connectedPrimitivesIndices.getInt(i);
            final int count = offset - prevOffset;
            offsets[i] = offset;
            counts[i - 1] = count;
            prevOffset = offset;
        }
        counts[multiDrawCount - 1] = this.numNextCalls - prevOffset;
    }
}
