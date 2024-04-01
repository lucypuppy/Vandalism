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

package de.nekosarekawaii.vandalism.integration.render.shader.multipass.impl;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.integration.render.shader.Shader;
import de.nekosarekawaii.vandalism.integration.render.shader.multipass.MultiPassShader;
import de.nekosarekawaii.vandalism.integration.render.shader.Shaders;
import de.nekosarekawaii.vandalism.integration.render.shader.uniform.UniformTypes;
import de.nekosarekawaii.vandalism.integration.render.shader.uniform.vector.Vec2i;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class BlurMultiPassShader extends MultiPassShader {

    private static final Shader SHADER = new Shader(
            new Identifier(FabricBootstrap.MOD_ID, "shader/vertex/vertex.vert"),
            new Identifier(FabricBootstrap.MOD_ID, "shader/fragment/gaussian.frag")
    ).addUniform(UniformTypes.VEC2_INT, "direction").addUniform(UniformTypes.INT, "tex")
            .addUniform(UniformTypes.FLOAT, "sigma")
            .addUniform(UniformTypes.VEC2, "resolution");

    private float strength = 15.0f;

    public BlurMultiPassShader() {
        // horizontal
        addPass(SHADER, "tex", ((shader, framebuffer) -> {
            shader.setUniform("sigma", strength);
            shader.setUniform("direction", new Vec2i(1, 0));
            shader.setUniform("resolution", new Vec2f((float) framebuffer.textureWidth, (float) framebuffer.textureHeight));
        }));

        // vertical
        addPass(SHADER, "tex", ((shader, framebuffer) -> {
            shader.setUniform("sigma", strength);
            shader.setUniform("direction", new Vec2i(0, 1));
            shader.setUniform("resolution", new Vec2f((float) framebuffer.textureWidth, (float) framebuffer.textureHeight));
        }));
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

}
