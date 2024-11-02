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
public enum StencilOp {

    KEEP(GL45C.GL_KEEP),
    ZERO(GL45C.GL_ZERO),
    REPLACE(GL45C.GL_REPLACE),
    INCR(GL45C.GL_INCR),
    INCR_WRAP(GL45C.GL_INCR_WRAP),
    DECR(GL45C.GL_DECR),
    DECR_WRAP(GL45C.GL_DECR_WRAP),
    INVERT(GL45C.GL_INVERT);

    private final int glType;

    public static StencilOp byGlType(int glType) {
        for (StencilOp op : values()) {
            if (op.glType == glType) {
                return op;
            }
        }
        return null;
    }
}
