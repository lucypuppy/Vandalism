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

package de.nekosarekawaii.vandalism.integration.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import de.nekosarekawaii.vandalism.integration.render.shader.uniform.Uniform;
import de.nekosarekawaii.vandalism.integration.render.shader.uniform.UniformTypes;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * A shader program used for rendering shaders in Minecraft.
 */
public class Shader implements MinecraftWrapper {

    private final int programID;
    private int previousProgramID;

    private final long startTime;

    private final HashMap<String, Uniform<?>> uniformCache;

    /**
     * Creates a new Shader with specified vertex and fragment shaders.
     *
     * @param vertexShader   The identifier for the vertex shader source.
     * @param fragmentShader The identifier for the fragment shader source.
     */
    public Shader(final Identifier vertexShader, final Identifier fragmentShader) {
        this.programID = GlStateManager.glCreateProgram();
        this.uniformCache = new HashMap<>();

        // Compile Shaders
        final int vertexShaderID = compileShader(vertexShader, GL20.GL_VERTEX_SHADER);
        final int fragmentShaderID = compileShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);

        // Link Shaders
        GlStateManager.glAttachShader(this.programID, vertexShaderID);
        GlStateManager.glAttachShader(this.programID, fragmentShaderID);
        GlStateManager.glLinkProgram(this.programID);

        this.startTime = System.currentTimeMillis();
    }

    /**
     * Adds a uniform to the shader program.
     *
     * @param type The type of uniform to add.
     * @param name The name of the uniform.
     * @return This Shader instance.
     */
    public Shader addUniform(final UniformTypes type, final String name) {
        if (this.uniformCache.containsKey(name))
            throw new IllegalStateException("Uniform already exists " + name);

        this.uniformCache.put(name, type.get(this.programID, name));
        return this;
    }

    /**
     * Sets the value of a uniform.
     *
     * @param name  The name of the uniform.
     * @param value The value to set.
     * @param <T>   The type of the uniform value.
     */
    @SuppressWarnings("unchecked")
    public <T> void setUniform(final String name, final T value) {
        final Uniform<T> uniform = (Uniform<T>) this.uniformCache.get(name);

        if (uniform == null)
            throw new IllegalStateException("Uniform does not exist " + name);

        uniform.setValue(value);
    }

    /**
     * Compiles the shader source code.
     *
     * @param shaderLocation The identifier for the shader source.
     * @param type           The type of shader.
     * @return The ID of the compiled shader.
     * @throws NullPointerException If the shader fails to compile.
     */
    private int compileShader(final Identifier shaderLocation, final int type) throws NullPointerException {
        final int shaderID = GlStateManager.glCreateShader(type);

        final StringBuilder source = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                MinecraftClient.getInstance()
                        .getResourceManager()
                        .getResourceOrThrow(shaderLocation)
                        .getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines().forEach(line -> source.append(line).append(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new NullPointerException("Failed to load shader source: " + e.getMessage());
        }

        GL20.glShaderSource(shaderID, source);
        GlStateManager.glCompileShader(shaderID);

        // check if it failed to compile
        if (GlStateManager.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) != GL20.GL_TRUE) {
            throw new NullPointerException("Failed to compile shader due to: \n" + GL20.glGetShaderInfoLog(shaderID));
        }

        return shaderID;
    }

    /**
     * Applies the shader program.
     */
    public void push() {
        this.previousProgramID = GlStateManager._getInteger(GL20.GL_CURRENT_PROGRAM);
        GlStateManager._glUseProgram(this.programID);
    }

    /**
     * Stops using the shader program.
     */
    public void pop() {
        pop(false);
    }

    /**
     * Stops using the shader program.
     *
     * @param delete Whether to delete the program completely.
     */
    public void pop(final boolean delete) {
        if (delete) {
            GlStateManager.glDeleteShader(0);
        } else {
            GlStateManager._glUseProgram(this.previousProgramID);
        }
    }

    /**
     * Renders the shader on the screen.
     *
     * @param matrices       The matrix stack.
     * @param updateUniforms A consumer to update uniforms before rendering.
     */
    public void drawOnScreen(final MatrixStack matrices, final Consumer<Shader> updateUniforms) {
        this.push();
        updateUniforms.accept(this);
        this.applyShaderVertices(matrices);
        this.pop(true);
    }

    /**
     * Applies shader vertices.
     *
     * @param matrices The matrix stack.
     */
    public void applyShaderVertices(final MatrixStack matrices) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(matrix, -1F, -1F, 0F).next();
        bufferBuilder.vertex(matrix, mc.getWindow().getFramebufferWidth(), -1F, 0F).next();
        bufferBuilder.vertex(matrix, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), 0F).next();
        bufferBuilder.vertex(matrix, -1F, mc.getWindow().getFramebufferHeight(), 0F).next();
        BufferRenderer.draw(bufferBuilder.end());
    }

    /**
     * Gets the start time of the shader.
     *
     * @return The start time in milliseconds.
     */
    public long getStartTime() {
        return startTime;
    }

}
