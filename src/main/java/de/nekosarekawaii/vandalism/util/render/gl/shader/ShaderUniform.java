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

package de.nekosarekawaii.vandalism.util.render.gl.shader;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL45C;

import java.awt.Color;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ShaderUniform {

    private final ShaderProgram owner;
    private final int id;

    public ShaderProgram owner() {
        return this.owner;
    }

    public int id() {
        return this.id;
    }

    public void set(float value) {
        GL45C.glProgramUniform1f(this.owner.id(), this.id, value);
    }

    public void set(double value) {
        GL45C.glProgramUniform1f(this.owner.id(), this.id, (float) value);
    }

    public void set(int value) {
        GL45C.glProgramUniform1i(this.owner.id(), this.id, value);
    }

    public void set(float x, float y) {
        GL45C.glProgramUniform2f(this.owner.id(), this.id, x, y);
    }

    public void set(Vector2fc vec) {
        this.set(vec.x(), vec.y());
    }

    public void set(float x, float y, float z) {
        GL45C.glProgramUniform3f(this.owner.id(), this.id, x, y, z);
    }

    public void set(Vector3fc vec) {
        this.set(vec.x(), vec.y(), vec.z());
    }

    public void set(float x, float y, float z, float w) {
        GL45C.glProgramUniform4f(this.owner.id(), this.id, x, y, z, w);
    }

    public void set(Vector4fc vec) {
        this.set(vec.x(), vec.y(), vec.z(), vec.w());
    }

    public void set(float[] mat, boolean transpose) {
        GL45C.glProgramUniformMatrix4fv(this.owner.id(), this.id, transpose, mat);
    }

    public void set(float[] mat) {
        this.set(mat, false);
    }

    public void set(Matrix4fc mat, boolean transpose) {
        this.set(mat.get(new float[16]), transpose);
    }

    public void set(Matrix4fc mat) {
        this.set(mat, false);
    }

    public void set(boolean value) {
        this.set(value ? 1 : 0);
    }

    public void set(Color color, boolean alpha) {
        if (alpha) {
            this.set(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            return;
        }

        this.set(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public void set(Color color) {
        this.set(color, true);
    }
}
