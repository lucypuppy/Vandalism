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

package de.nekosarekawaii.vandalism.util.render;

import de.nekosarekawaii.vandalism.util.render.gl.mem.BufferPool;
import de.nekosarekawaii.vandalism.util.render.gl.mem.VertexArrayPool;
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
