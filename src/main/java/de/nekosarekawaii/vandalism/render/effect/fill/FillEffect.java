/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

import de.nekosarekawaii.vandalism.render.effect.PostProcessEffect;
import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;

public abstract class FillEffect extends PostProcessEffect {

    public FillEffect(String name) {
        super(name);
    }

    protected void addDefaultPass() {
        this.addPass(ctx -> {
            ctx.setShader(this.getShaderPath());
            ctx.setTextureBinding("tex", this.maskFramebuffer());
            ctx.setUniformSetup(this::setupUniforms);
            this.initPass(ctx);
        });
    }

    protected void initPass(PassInitContext ctx) {
    }

    protected abstract String getShaderPath();

    protected void setupUniforms(ShaderProgram shader) {
    }
}
