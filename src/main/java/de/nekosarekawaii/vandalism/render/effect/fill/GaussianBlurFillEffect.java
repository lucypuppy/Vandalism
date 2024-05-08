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

package de.nekosarekawaii.vandalism.render.effect.fill;

import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;
import lombok.Getter;
import lombok.Setter;

public class GaussianBlurFillEffect extends FillEffect {

    @Getter
    @Setter
    private int textureId;

    @Getter
    @Setter
    private float directions = 16.0f;

    @Getter
    @Setter
    private float quality = 4.0f;

    @Getter
    @Setter
    private float radius = 8.0f;

    public GaussianBlurFillEffect() {
        super("GaussianBlurFill");
        this.addDefaultPass();
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/gaussian_blur_fill";
    }

    @Override
    protected void initPass(PassInitContext ctx) {
        super.initPass(ctx);
        ctx.setTextureBinding("mask", this.maskFramebuffer());
        ctx.setTextureBinding("tex", () -> this.textureId);
    }

    @Override
    protected void setupUniforms(ShaderProgram shader) {
        super.setupUniforms(shader);
        shader.uniform("u_BlurValues").set(this.directions, this.quality, this.radius);
    }

    public void configureDefault() {
        this.directions = 16.0f;
        this.quality = 4.0f;
        this.radius = 8.0f;
    }
}