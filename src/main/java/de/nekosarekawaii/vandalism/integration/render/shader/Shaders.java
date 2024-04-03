/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.render.shader;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.integration.render.shader.uniform.UniformTypes;
import net.minecraft.util.Identifier;

public enum Shaders {

    BACKGROUND(new Shader(
            new Identifier(FabricBootstrap.MOD_ID, "shader/vertex/vertex.vert"),
            new Identifier(FabricBootstrap.MOD_ID, "shader/fragment/background.frag")
    ).addUniform(UniformTypes.VEC2, "resolution")
            .addUniform(UniformTypes.FLOAT, "time")
            .addUniform(UniformTypes.VEC3, "color1")
            .addUniform(UniformTypes.VEC3, "color2")
            .addUniform(UniformTypes.VEC3, "color3"));

    private final Shader shader;

    Shaders(final Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

}
