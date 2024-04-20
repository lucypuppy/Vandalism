package de.evilcodez.supermod.render.gl.mem;

import de.evilcodez.supermod.render.gl.vertex.VAO;
import de.evilcodez.supermod.render.gl.vertex.VertexLayout;
import de.evilcodez.supermod.render.gl.vertex.VertexLayoutElement;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Queue;

public class VertexArrayPool implements AutoCloseable {

    private final Queue<VAO> vaoQueue;
    private final Map<VAO, VertexLayout> borrowedVAOs;

    public VertexArrayPool(int initialVaoCount) {
        this.vaoQueue = new ArrayDeque<>();
        this.borrowedVAOs = new IdentityHashMap<>();
        for (int i = 0; i < initialVaoCount; i++) {
            this.vaoQueue.add(new VAO());
        }
    }

    public int getBufferCount() {
        return this.vaoQueue.size();
    }

    public VAO borrowVAO(VertexLayout layout) {
        VAO vao = this.vaoQueue.poll();
        if (vao == null) {
            vao = new VAO();
        }
        this.borrowedVAOs.put(vao, layout);
        return vao;
    }

    public void returnVAO(VAO vao) {
        final VertexLayout layout = this.borrowedVAOs.remove(vao);
        if (layout == null) throw new IllegalArgumentException("VAO not borrowed from this pool");
        for (VertexLayoutElement element : layout.elements()) {
            vao.disableAttrib(element.attribIndex());
        }
        this.vaoQueue.add(vao);
    }

    @Override
    public void close() {
        for (VAO vao : this.vaoQueue) {
            vao.close();
        }
        this.vaoQueue.clear();
    }
}
