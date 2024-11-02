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
import lombok.Getter;
import lombok.Setter;

public class GlowOutlineEffect extends OutlineEffect {

    @Getter
    @Setter
    private float exponent = 1.0f;

    public GlowOutlineEffect() {
        super("GlowOutline");
    }

    protected GlowOutlineEffect(String name) {
        super(name);
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/outline/glow_outline";
    }

    @Override
    protected void setupAdditionalUniforms(ShaderProgram shader) {
        shader.uniform("u_Exponent").set(this.exponent);
    }

    public void configure(float outlineWidth, float outlineAccuracy, float exponent) {
        this.setOutlineWidth(outlineWidth);
        this.setOutlineAccuracy(outlineAccuracy);
        this.exponent = exponent;
    }
}
