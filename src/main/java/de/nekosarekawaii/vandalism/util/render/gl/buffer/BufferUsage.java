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
public enum BufferUsage {

    STREAM_DRAW(GL45C.GL_STREAM_DRAW),
    STREAM_READ(GL45C.GL_STREAM_READ),
    STREAM_COPY(GL45C.GL_STREAM_COPY),
    STATIC_DRAW(GL45C.GL_STATIC_DRAW),
    STATIC_READ(GL45C.GL_STATIC_READ),
    STATIC_COPY(GL45C.GL_STATIC_COPY),
    DYNAMIC_DRAW(GL45C.GL_DYNAMIC_DRAW),
    DYNAMIC_READ(GL45C.GL_DYNAMIC_READ),
    DYNAMIC_COPY(GL45C.GL_DYNAMIC_COPY);

    private final int glType;

    public static BufferUsage byGlType(int glType) {
        for (BufferUsage usage : values()) {
            if (usage.glType == glType) {
                return usage;
            }
        }
        return null;
    }
}
