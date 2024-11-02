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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL33C;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Shader implements AutoCloseable {

    private final ShaderType type;
    private final int id;

    public static Shader compile(ShaderType type, String source) {
        final int id = GL33C.glCreateShader(type.getGlType());
        GL33C.glShaderSource(id, source);
        GL33C.glCompileShader(id);
        final int status = GL33C.glGetShaderi(id, GL33C.GL_COMPILE_STATUS);
        if (status == GL33C.GL_FALSE) {
            final String log = GL33C.glGetShaderInfoLog(id);
            GL33C.glDeleteShader(id);
            throw new ShaderException("Failed to compile shader: " + log);
        }
        return new Shader(type, id);
    }

    public ShaderType type() {
        return this.type;
    }

    public int id() {
        return this.id;
    }

    @Override
    public void close() {
        GL33C.glDeleteShader(this.id);
    }
}
