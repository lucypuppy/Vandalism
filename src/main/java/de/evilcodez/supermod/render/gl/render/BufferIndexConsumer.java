package de.evilcodez.supermod.render.gl.render;

import de.evilcodez.supermod.render.gl.mem.ByteBufferBuilder;
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
