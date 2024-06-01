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

import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class ColorFillEffect extends FillEffect {

    @Getter
    @Setter
    private Color color = Color.WHITE;

    public ColorFillEffect() {
        super("ColorFill");
        this.addDefaultPass();
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/fill/color_fill";
    }

    @Override
    protected void setupUniforms(ShaderProgram shader) {
        super.setupUniforms(shader);
        shader.uniform("u_FillColor").set(this.color);
    }
}
