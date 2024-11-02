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

package de.nekosarekawaii.vandalism.util.render.gl.render.passes;

import de.nekosarekawaii.vandalism.util.render.gl.render.PrimitiveType;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.gl.vertex.VertexLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderPass {

    void setupState();

    void cleanupState();

    /**
     * Set up the uniforms for the given program.
     * @param program The program to set up the uniforms for.
     * @param instanceCount The number of instances to render. -1 if not instanced.
     */
    void setupUniforms(ShaderProgram program, int instanceCount);

    ShaderProgram getShaderProgram();

    default ShaderProgram getInstancedShaderProgram() {
        return null;
    }

    /**
     * @return The layout of the vertex attributes.
     */
    @NotNull
    VertexLayout getVertexLayout();

    @Nullable
    default VertexLayout getInstanceLayout() {
        return null;
    }

    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.TRIANGLES;
    }
}
