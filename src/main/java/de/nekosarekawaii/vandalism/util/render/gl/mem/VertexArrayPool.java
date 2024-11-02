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

package de.nekosarekawaii.vandalism.util.render.gl.mem;

import de.nekosarekawaii.vandalism.util.render.gl.vertex.VAO;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayout;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayoutElement;

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
