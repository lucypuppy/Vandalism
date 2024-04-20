package de.evilcodez.supermod.render.gl.mem;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Queue;

public class BufferPool implements AutoCloseable {

    @Getter
    private final int bufferCacheLimit;
    @Getter
    private final int initialBufferCapacity;
    private final Queue<ByteBufferBuilder> bufferQueue;
    @Getter
    private int borrowedBuffers;

    public BufferPool(int bufferCacheLimit, int initialBufferCount, int initialBufferCapacity) {
        this.bufferCacheLimit = bufferCacheLimit;
        this.initialBufferCapacity = initialBufferCapacity;
        this.bufferQueue = new ArrayDeque<>();
        final int count = Math.min(initialBufferCount, bufferCacheLimit);
        for (int i = 0; i < count; i++) {
            this.bufferQueue.add(new ByteBufferBuilder(initialBufferCapacity));
        }
    }

    public int getBufferCount() {
        return this.bufferQueue.size();
    }

    public ByteBufferBuilder borrowBuffer() {
        ByteBufferBuilder buffer = this.bufferQueue.poll();
        if (buffer == null) {
            buffer = new ByteBufferBuilder(this.initialBufferCapacity);
        }
        buffer.reset();
        ++this.borrowedBuffers;
        return buffer;
    }

    public void returnBuffer(ByteBufferBuilder buffer) {
        buffer.reset();
        --this.borrowedBuffers;
        if (this.bufferQueue.size() >= this.bufferCacheLimit) {
            buffer.close();
            return;
        }
        this.bufferQueue.add(buffer);
    }

    @Override
    public void close() {
        for (ByteBufferBuilder buffer : this.bufferQueue) {
            buffer.close();
        }
        this.bufferQueue.clear();
        this.borrowedBuffers = 0;
    }
}
