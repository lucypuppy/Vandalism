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

package de.nekosarekawaii.vandalism.util.render.gl.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum PrimitiveType {

    TRIANGLES(GL45C.GL_TRIANGLES, false),
    TRIANGLE_STRIP(GL45C.GL_TRIANGLE_STRIP, true),
    TRIANGLE_FAN(GL45C.GL_TRIANGLE_FAN, true),
    QUADS(GL45C.GL_TRIANGLES, false),
    POINTS(GL45C.GL_POINTS, false),
    GL_LINES(GL45C.GL_LINES, false),
    GL_LINE_STRIP(GL45C.GL_LINE_STRIP, true),
    GL_LINE_LOOP(GL45C.GL_LINE_LOOP, true),
    MINECRAFT_LINES(GL45C.GL_TRIANGLES, false),
    MINECRAFT_LINE_STRIP(GL45C.GL_TRIANGLE_STRIP, true);

    private final int glType;
    private final boolean connectedPrimitive;

    public static PrimitiveType byGlType(int glType) {
        for (PrimitiveType type : values()) {
            if (type.glType == glType) {
                return type;
            }
        }
        return null;
    }
}
