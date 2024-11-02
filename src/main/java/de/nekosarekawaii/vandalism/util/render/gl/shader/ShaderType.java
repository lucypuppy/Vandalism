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

package de.nekosarekawaii.vandalism.util.render.gl.shader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum ShaderType {

    VERTEX(GL45C.GL_VERTEX_SHADER, "vertex"),
    FRAGMENT(GL45C.GL_FRAGMENT_SHADER, "fragment"),
    GEOMETRY(GL45C.GL_GEOMETRY_SHADER, "geometry"),
    TESS_CONTROL(GL45C.GL_TESS_CONTROL_SHADER, "tess_control"),
    TESS_EVALUATION(GL45C.GL_TESS_EVALUATION_SHADER, "tess_evaluation"),
    COMPUTE(GL45C.GL_COMPUTE_SHADER, "compute");

    private final int glType;
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
