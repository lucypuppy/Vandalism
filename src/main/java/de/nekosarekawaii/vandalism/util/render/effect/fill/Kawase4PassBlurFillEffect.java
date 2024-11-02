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

package de.nekosarekawaii.vandalism.util.render.effect.fill;

import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Kawase4PassBlurFillEffect extends FillEffect {

    private int textureId;

    private float radius = 3.0f;

    public Kawase4PassBlurFillEffect() {
        super("Kawase4PassBlurFill");
        this.addPass(ctx -> {
            ctx.setShader("postprocess/fill/kawase4passblurfill");
            ctx.setTextureBinding("u_Mask", this.maskFramebuffer());
            ctx.setTextureBinding("iChannel0", () -> this.textureId);
            ctx.setUniformSetup(shader -> this.setupUniforms(shader, 0));
        });
        this.addPass(ctx -> {
            ctx.setShader(0);
            ctx.setTextureBinding("u_Mask", this.maskFramebuffer());
            ctx.setTextureBinding("iChannel0", this.framebuffer(0));
            ctx.setUniformSetup(shader -> this.setupUniforms(shader, 1));
        });
        this.addPass(ctx -> {
            ctx.setShader(0);
            ctx.setTextureBinding("u_Mask", this.maskFramebuffer());
            ctx.setTextureBinding("iChannel0", this.framebuffer(1));
            ctx.setUniformSetup(shader -> this.setupUniforms(shader, 2));
        });
        this.addPass(ctx -> {
            ctx.setShader(0);
            ctx.setTextureBinding("u_Mask", this.maskFramebuffer());
            ctx.setTextureBinding("iChannel0", this.framebuffer(2));
            ctx.setUniformSetup(shader -> this.setupUniforms(shader, 3));
        });
    }

    @Override
    protected String getShaderPath() { // We don't use the default pass
        return null;
    }

    protected void setupUniforms(ShaderProgram shader, int pass) {
        super.setupUniforms(shader);
        shader.uniform("u_Pass").set(pass);
        shader.uniform("u_BlurRadius").set(this.radius);
    }
}