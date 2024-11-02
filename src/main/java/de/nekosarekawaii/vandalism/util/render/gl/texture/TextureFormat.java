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

package de.nekosarekawaii.vandalism.util.render.gl.texture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum TextureFormat {

    RED(GL45C.GL_RED),
    RG(GL45C.GL_RG),
    RGB(GL45C.GL_RGB),
    BGR(GL45C.GL_BGR),
    RGBA(GL45C.GL_RGBA),
    BGRA(GL45C.GL_BGRA),
    RED_INTEGER(GL45C.GL_RED_INTEGER),
    RG_INTEGER(GL45C.GL_RG_INTEGER),
    RGB_INTEGER(GL45C.GL_RGB_INTEGER),
    BGR_INTEGER(GL45C.GL_BGR_INTEGER),
    RGBA_INTEGER(GL45C.GL_RGBA_INTEGER),
    BGRA_INTEGER(GL45C.GL_BGRA_INTEGER),
    DEPTH_COMPONENT(GL45C.GL_DEPTH_COMPONENT),
    DEPTH_STENCIL(GL45C.GL_DEPTH_STENCIL),
    STENCIL_INDEX(GL45C.GL_STENCIL_INDEX);

    private final int glType;

    public static TextureFormat byGlType(int glType) {
        for (TextureFormat format : values()) {
            if (format.glType == glType) {
                return format;
            }
        }
        return null;
    }
}
