package de.evilcodez.supermod.render.gl.buffer;

import java.nio.ByteBuffer;

public record MappedBuffer(BufferObject object, ByteBuffer buffer) implements AutoCloseable {

    @Override
    public void close() {
        this.object.unmap();
    }
}
