package de.evilcodez.supermod.render.gl.render;

import de.evilcodez.supermod.render.gl.mem.ByteBufferBuilder;

public class IndirectCommands {

    public static int writeDrawArraysIndirect(ByteBufferBuilder dest, int count, int instanceCount, int firstVertex, int baseInstance) {
        dest.putInt(count);
        dest.putInt(instanceCount);
        dest.putInt(firstVertex);
        dest.putInt(baseInstance);
        return 16;
    }

    public static int writeDrawElementsIndirect(ByteBufferBuilder dest, int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance) {
        dest.putInt(count);
        dest.putInt(instanceCount);
        dest.putInt(firstIndex);
        dest.putInt(baseVertex);
        dest.putInt(baseInstance);
        return 20;
    }
}
