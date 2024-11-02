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
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString
@RequiredArgsConstructor
public class UniqueRenderPass implements RenderPass {

    private final RenderPass pass;

    @Override
    public void setupState() {
        this.pass.setupState();
    }

    @Override
    public void cleanupState() {
        this.pass.cleanupState();
    }

    @Override
    public void setupUniforms(ShaderProgram program, int instanceCount) {
        this.pass.setupUniforms(program, instanceCount);
    }

    @Override
    public ShaderProgram getShaderProgram() {
        return this.pass.getShaderProgram();
    }

    @Override
    public ShaderProgram getInstancedShaderProgram() {
        return this.pass.getInstancedShaderProgram();
    }

    @Override
    public @NotNull VertexLayout getVertexLayout() {
        return this.pass.getVertexLayout();
    }

    @Override
    public @Nullable VertexLayout getInstanceLayout() {
        return this.pass.getInstanceLayout();
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return this.pass.getPrimitiveType();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
