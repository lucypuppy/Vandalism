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

package de.nekosarekawaii.vandalism.integration.render.shader.uniform.impl;

import de.nekosarekawaii.vandalism.integration.render.shader.uniform.Uniform;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20C;

public class V4fUniform extends Uniform<Vector4f> {

    public V4fUniform(final int programID, final String uniformName) {
        super(programID, uniformName);
    }

    @Override
    public void apply() {
        GL20C.glUniform4f(this.location, this.value.x, this.value.y, this.value.z, this.value.w);
    }

}