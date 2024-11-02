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

package de.nekosarekawaii.vandalism.util.render.gl.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum StencilFunc {

    NEVER(GL45C.GL_NEVER),
    LESS(GL45C.GL_LESS),
    LEQUAL(GL45C.GL_LEQUAL),
    GREATER(GL45C.GL_GREATER),
    GEQUAL(GL45C.GL_GEQUAL),
    EQUAL(GL45C.GL_EQUAL),
    NOTEQUAL(GL45C.GL_NOTEQUAL),
    ALWAYS(GL45C.GL_ALWAYS);

    private final int glType;

    public static StencilFunc byGlType(int glType) {
        for (StencilFunc func : values()) {
            if (func.glType == glType) {
                return func;
            }
        }
        return null;
    }
}
