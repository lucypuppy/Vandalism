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

package de.nekosarekawaii.vandalism.util.render.gl.buffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum BufferTarget {
    ARRAY_BUFFER(GL45C.GL_ARRAY_BUFFER),
    ATOMIC_COUNTER_BUFFER(GL45C.GL_ATOMIC_COUNTER_BUFFER),
    COPY_READ_BUFFER(GL45C.GL_COPY_READ_BUFFER),
    COPY_WRITE_BUFFER(GL45C.GL_COPY_WRITE_BUFFER),
    DISPATCH_INDIRECT_BUFFER(GL45C.GL_DISPATCH_INDIRECT_BUFFER),
    DRAW_INDIRECT_BUFFER(GL45C.GL_DRAW_INDIRECT_BUFFER),
    ELEMENT_ARRAY_BUFFER(GL45C.GL_ELEMENT_ARRAY_BUFFER),
    PIXEL_PACK_BUFFER(GL45C.GL_PIXEL_PACK_BUFFER),
    PIXEL_UNPACK_BUFFER(GL45C.GL_PIXEL_UNPACK_BUFFER),
    QUERY_BUFFER(GL45C.GL_QUERY_BUFFER),
    SHADER_STORAGE_BUFFER(GL45C.GL_SHADER_STORAGE_BUFFER),
    TEXTURE_BUFFER(GL45C.GL_TEXTURE_BUFFER),
    TRANSFORM_FEEDBACK_BUFFER(GL45C.GL_TRANSFORM_FEEDBACK_BUFFER),
    UNIFORM_BUFFER(GL45C.GL_UNIFORM_BUFFER);

    private final int glType;
}
