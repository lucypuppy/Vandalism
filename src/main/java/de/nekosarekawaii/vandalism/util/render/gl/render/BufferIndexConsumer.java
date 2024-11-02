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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class BufferIndexConsumer implements IndexConsumer {

    private final BufferCollection buffers;
    private final ByteBufferBuilder buffer;
    private final IndexType type;
    private int indexCount;
    @Setter
    private int baseOffset;

    @Override
    public IndexConsumer index(int index) {
        index += this.baseOffset;
        switch (this.type) {
            case UNSIGNED_BYTE -> this.buffer.putByte((byte) index);
            case UNSIGNED_SHORT -> this.buffer.putShort((short) index);
            case UNSIGNED_INT -> this.buffer.putInt(index);
        }
        ++this.indexCount;
        return this;
    }

    @Override
    public IndexConsumer applyBaseOffset() {
        if (this.buffers.getMainBuffer() != null) {
            this.baseOffset = this.buffers.getMainBuffer().getVertexNumNextCalls();
        }
        return this;
    }
}
