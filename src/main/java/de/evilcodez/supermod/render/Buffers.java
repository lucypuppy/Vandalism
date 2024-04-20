package de.evilcodez.supermod.render;

import de.evilcodez.supermod.render.gl.mem.BufferPool;
import de.evilcodez.supermod.render.gl.mem.VertexArrayPool;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import lombok.Getter;
import org.lwjgl.opengl.GL33C;

public class Buffers {

    @Getter
    private static final BufferPool immediateBufferPool = new BufferPool(1024, 4, 262144);
    @Getter
    private static final BufferPool persistentBufferPool = new BufferPool(64, 4, 65536);
    @Getter
    private static final VertexArrayPool vertexArrayPool = new VertexArrayPool(1);

    private static final IntStack bufferStack = new IntArrayList();
    private static int lastSavedBufferId;

    public static int saveBuffer() {
        final int bufferId = GL33C.glGetInteger(GL33C.GL_FRAMEBUFFER_BINDING);
        bufferStack.push(bufferId);
        return bufferId;
    }

    public static int restoreBuffer() {
        final int bufferId = bufferStack.isEmpty() ? lastSavedBufferId : (lastSavedBufferId = bufferStack.popInt());
        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, bufferId);
        return bufferId;
    }

    public static void bindBuffer(int bufferId) {
        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, bufferId);
    }
}
