package de.evilcodez.supermod.render.gl.mem;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufferAlloc {

    public static ByteBuffer allocate(int size) {
        return MemoryUtil.memAlloc(size);
    }

    public static void free(ByteBuffer buffer) {
        MemoryUtil.memFree(buffer);
    }

    public static ByteBuffer realloc(ByteBuffer buffer, int size) {
        return MemoryUtil.memRealloc(buffer, size);
    }
}
