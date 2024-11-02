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
