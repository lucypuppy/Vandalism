/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.gl.utils.TemporaryValues;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;

public class GlobalUniforms implements MinecraftWrapper {

    public static void setGlobalUniforms(ShaderProgram program, boolean setIdentityTransform) {
        program.uniform("u_ModelViewMatrix").set(RenderSystem.getModelViewMatrix());
        program.uniform("u_ProjectionMatrix").set(RenderSystem.getProjectionMatrix());
        program.uniform("u_WindowSize").set((float) mc.getWindow().getWidth(), (float) mc.getWindow().getHeight());
        program.uniform("u_ScaleFactor").set((float) mc.getWindow().getScaleFactor());
        program.uniform("u_Time").set((float) (GLFW.glfwGetTime() - Vandalism.getInstance().getStartTime()));
        if (setIdentityTransform) setTransformMatrix(program, TemporaryValues.IDENTITY_MATRIX4F);
    }

    public static void setTransformMatrix(ShaderProgram program, Matrix4fc matrix) {
        program.uniform("u_TransformMatrix").set(matrix);
    }

    public static void setBackgroundUniforms(ShaderProgram program) {
        program.uniform("resolution").set((float) mc.getWindow().getWidth(), (float) mc.getWindow().getHeight());
        program.uniform("time").set((float) (GLFW.glfwGetTime() - Vandalism.getInstance().getStartTime()));
    }

}
