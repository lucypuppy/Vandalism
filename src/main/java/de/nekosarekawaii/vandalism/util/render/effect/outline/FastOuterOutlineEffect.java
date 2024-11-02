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

package de.nekosarekawaii.vandalism.util.render.effect.outline;

import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;

public class FastOuterOutlineEffect extends OutlineEffect {

    public FastOuterOutlineEffect() {
        super("FastOuterOutline");
        this.addPass(ctx -> { // Adds the second pass to the effect
            ctx.setShader(0);
            ctx.setTextureBinding("mask", this.maskFramebuffer());
            ctx.setTextureBinding("tex", this.framebuffer(0));
            ctx.setUniformSetup(shader -> {
                shader.uniform("u_OutlineWidth").set(this.getOutlineWidth());
                shader.uniform("u_OutlineAccuracy").set(this.getOutlineAccuracy());
                shader.uniform("u_Pass").set(1);
            });
        });
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/outline/fast_outer_outline";
    }

    @Override
    protected void setupAdditionalUniforms(ShaderProgram shader) {
        super.setupAdditionalUniforms(shader);
        shader.uniform("u_Pass").set(0);
    }

    public void configure(float outlineWidth, float outlineAccuracy) {
        this.setOutlineWidth(outlineWidth);
        this.setOutlineAccuracy(outlineAccuracy);
    }
}
