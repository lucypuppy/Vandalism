/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.integration.render.shader.uniform;

import org.lwjgl.opengl.GL20C;

public abstract class Uniform<T> {

    public final int location;
    public T value;

    public Uniform(final int programID, final String uniformName) {
        this.location = GL20C.glGetUniformLocation(programID, uniformName);
    }

    public void setValue(final T value) {
        if (this.value == value)
            return;

        this.value = value;
        apply();
    }

    public abstract void apply();

}