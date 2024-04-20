package de.evilcodez.supermod.render.gl.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import de.evilcodez.supermod.render.gl.utils.TemporaryValues;
import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4fc;
import org.lwjgl.glfw.GLFW;

public class GlobalUniforms {

    public static void setGlobalUniforms(ShaderProgram program, boolean setIdentityTransform) {
        final MinecraftClient mc = MinecraftClient.getInstance();
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
}
