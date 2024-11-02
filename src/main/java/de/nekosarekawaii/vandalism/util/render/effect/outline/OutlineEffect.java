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

import de.nekosarekawaii.vandalism.util.render.effect.PostProcessEffect;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import lombok.Getter;
import lombok.Setter;

public abstract class OutlineEffect extends PostProcessEffect {

    @Getter
    @Setter
    private float outlineWidth = 2.0f;

    @Getter
    @Setter
    private float outlineAccuracy = 1.0f;

    public OutlineEffect(String name) {
        super(name);
        this.addPass(ctx -> {
            ctx.setShader(this.getShaderPath());
            ctx.setTextureBinding("tex", this.maskFramebuffer());
            ctx.setUniformSetup(shader -> {
                shader.uniform("u_OutlineWidth").set(this.outlineWidth);
                shader.uniform("u_OutlineAccuracy").set(this.outlineAccuracy);
                this.setupAdditionalUniforms(shader);
            });
        });
    }

    protected abstract String getShaderPath();

    protected void setupAdditionalUniforms(ShaderProgram shader) {}

    @Override
    public void reset() {
        this.outlineAccuracy = 1.0f;
    }
}
