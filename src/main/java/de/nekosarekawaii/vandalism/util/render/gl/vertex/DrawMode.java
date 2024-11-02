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

package de.nekosarekawaii.vandalism.util.render.gl.vertex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum DrawMode {

    POINTS("Points", GL45C.GL_POINTS),
    LINE_STRIP("Line-Strip", GL45C.GL_LINE_STRIP),
    LINE_LOOP("Line-Loop", GL45C.GL_LINE_LOOP),
    LINES("Lines", GL45C.GL_LINES),
    LINE_STRIP_ADJACENCY("Line-Strip-Adjacency", GL45C.GL_LINE_STRIP_ADJACENCY),
    TRIANGLE_STRIP("Triangle-Strip", GL45C.GL_TRIANGLE_STRIP),
    TRIANGLE_FAN("Triangle-Fan", GL45C.GL_TRIANGLE_FAN),
    TRIANGLES("Triangles", GL45C.GL_TRIANGLES),
    TRIANGLE_STRIP_ADJACENCY("Triangle-Strip-Adjacency", GL45C.GL_TRIANGLE_STRIP_ADJACENCY),
    TRIANGLES_ADJACENCY("Triangles-Adjacency", GL45C.GL_TRIANGLES_ADJACENCY),
    PATCHES("Patches", GL45C.GL_PATCHES);

    private final String name;
    private final int glType;

    @Override
    public String toString() {
        return name;
    }

    public static DrawMode byGlType(int glType) {
        for (DrawMode mode : values()) {
            if (mode.glType == glType) {
                return mode;
            }
        }
        return null;
    }
}
