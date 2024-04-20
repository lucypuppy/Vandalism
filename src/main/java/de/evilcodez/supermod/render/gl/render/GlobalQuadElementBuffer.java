package de.evilcodez.supermod.render.gl.render;

import de.evilcodez.supermod.render.gl.buffer.BufferObject;
import de.evilcodez.supermod.render.gl.buffer.BufferUsage;
import de.evilcodez.supermod.render.gl.mem.BufferAlloc;
import de.nekosarekawaii.vandalism.Vandalism;

import java.nio.ByteBuffer;

public class GlobalQuadElementBuffer {

    public static final IndexType QUAD_ELEMENT_BUFFER_TYPE = IndexType.UNSIGNED_INT;
    private static final int INITIAL_QUAD_CAPACITY = 4096;
    private static BufferObject quadElementBuffer;
    private static int bufferQuadCapacity;

    /**
     * @param numQuads The number of quads that you want to draw.
     * @return An element buffer that contains quad to triangle conversion indices for at least numQuads quads. The buffer is forever reusable because it will never be deallocated and also never shrunk again.
     */
    public static BufferObject getQuadElementBuffer(int numQuads) {
        if (quadElementBuffer == null) {
            int quadCap = numQuads;
            if (quadCap % 4096 != 0) quadCap += 4096 - (quadCap % 4096);
            createBuffer(Math.max(INITIAL_QUAD_CAPACITY, quadCap));
        } else if (bufferQuadCapacity < numQuads) {
            int newCap = numQuads;
            if (newCap % 4096 != 0) newCap += 4096 - (newCap % 4096);
            createBuffer(newCap);
        }
        return quadElementBuffer;
    }

    private static void createBuffer(int quadCapacity) {
        if (quadCapacity <= bufferQuadCapacity) return;
        Vandalism.getInstance().getLogger().debug("Creating new quad element buffer with capacity " + quadCapacity);

        final ByteBuffer buffer = BufferAlloc.allocate(quadCapacity * 6 * 4); // quadCapacity * numVerticesPerQuad * sizeof(int)
        try {
            fillQuadIndices(buffer, 0, quadCapacity);
            if (quadElementBuffer == null) quadElementBuffer = new BufferObject();

            quadElementBuffer.init(buffer.flip(), BufferUsage.STATIC_DRAW);
            bufferQuadCapacity = quadCapacity;
        } finally {
            BufferAlloc.free(buffer);
        }
    }

    public static void fillQuadIndices(ByteBuffer dest, int offset, int numQuads) {
        for (int i = 0; i < numQuads; i++) {
            dest.putInt(offset + i * 4);
            dest.putInt(offset + i * 4 + 1);
            dest.putInt(offset + i * 4 + 2);
            dest.putInt(offset + i * 4 + 2);
            dest.putInt(offset + i * 4 + 3);
            dest.putInt(offset + i * 4);
        }
    }
}
