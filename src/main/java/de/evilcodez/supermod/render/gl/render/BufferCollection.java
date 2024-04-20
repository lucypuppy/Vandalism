package de.evilcodez.supermod.render.gl.render;

import de.evilcodez.supermod.render.gl.mem.BufferPool;
import de.evilcodez.supermod.render.gl.render.passes.RenderPass;
import de.evilcodez.supermod.render.gl.vertex.VertexLayout;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class BufferCollection implements AttribConsumerSet, AutoCloseable {

    private final BufferPool bufferPool;
    private final RenderPass pass;
    private final VertexLayout mainLayout;
    private BufferAttribConsumer mainBuffer;
    private BufferIndexConsumer indexDataBuffer;
    private final Object2ObjectMap<VertexLayout, BufferAttribConsumer> customBuffers;

    public BufferCollection(BufferPool bufferPool, RenderPass pass) {
        this.bufferPool = bufferPool;
        this.pass = pass;
        this.mainLayout = pass.getVertexLayout();
        this.customBuffers = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public @NotNull InstancedAttribConsumer main() {
        if (this.mainBuffer == null) {
            this.mainBuffer = new BufferAttribConsumer(this.bufferPool.borrowBuffer());
        }
        return this.mainBuffer;
    }

    @Override
    public @NotNull IndexConsumer indexData(IndexType type) {
        if (this.indexDataBuffer == null) {
            this.indexDataBuffer = new BufferIndexConsumer(this, this.bufferPool.borrowBuffer(), type);
        } else if (type != this.indexDataBuffer.getType()) {
            throw new IllegalStateException("Cannot change index type after it has been set");
        }
        if (this.mainBuffer != null) {
            this.indexDataBuffer.setBaseOffset(this.mainBuffer.getVertexNumNextCalls());
        }
        return this.indexDataBuffer;
    }

    @Override
    public @NotNull AttribConsumer custom(@NotNull VertexLayout layout) {
        return this.customBuffers.computeIfAbsent(layout, l -> new BufferAttribConsumer(this.bufferPool.borrowBuffer()));
    }

    public void reset() {
        if (this.mainBuffer != null) {
            this.bufferPool.returnBuffer(this.mainBuffer.getBuffer());
            this.mainBuffer = null;
        }
        if (this.indexDataBuffer != null) {
            this.bufferPool.returnBuffer(this.indexDataBuffer.getBuffer());
            this.indexDataBuffer = null;
        }
        for (BufferAttribConsumer builder : this.customBuffers.values()) {
            this.bufferPool.returnBuffer(builder.getBuffer());
        }
        this.customBuffers.clear();
    }

    @Override
    public void close() {
        this.reset();
    }
}
