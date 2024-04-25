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

package de.nekosarekawaii.vandalism.render.gl.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.render.gl.utils.TemporaryValues;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

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

    public static void setBackgroundUniforms(ShaderProgram program, Color color1, Color color2, Color color3) {
        program.uniform("resolution").set((float) mc.getWindow().getWidth(), (float) mc.getWindow().getHeight());
        program.uniform("time").set((float) (GLFW.glfwGetTime() - Vandalism.getInstance().getStartTime()));
        program.uniform("color1").set(
                color1.getRed() / 255.0f,
                color1.getGreen() / 255.0f,
                color1.getBlue() / 255.0f
        );
        program.uniform("color2").set(
                color2.getRed() / 255.0f,
                color2.getGreen() / 255.0f,
                color2.getBlue() / 255.0f
        );
        program.uniform("color3").set(
                color3.getRed() / 255.0f,
                color3.getGreen() / 255.0f,
                color3.getBlue() / 255.0f
        );
    }

}
