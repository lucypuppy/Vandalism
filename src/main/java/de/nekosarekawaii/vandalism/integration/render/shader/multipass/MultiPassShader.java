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

package de.nekosarekawaii.vandalism.integration.render.shader.multipass;


import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.integration.render.shader.FramebufferWrapper;
import de.nekosarekawaii.vandalism.integration.render.shader.Shader;
import de.nekosarekawaii.vandalism.integration.render.shader.uniform.UniformTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D;

public abstract class MultiPassShader {

    private static final Shader TRANSPARENT = new Shader(
            new Identifier(FabricBootstrap.MOD_ID, "shader/vertex/vertex.vert"),
            new Identifier(FabricBootstrap.MOD_ID, "shader/fragment/transparent.frag")
    ).addUniform(UniformTypes.INT, "inputTexture")
            .addUniform(UniformTypes.INT, "outputTexture")
            .addUniform(UniformTypes.VEC2, "resolution");

    private final FramebufferWrapper postProcessingBuffer = new FramebufferWrapper();
    private final FramebufferWrapper endBuffer = new FramebufferWrapper();

    private final List<Integer> textures = new ArrayList<>();
    private final List<Pass> passes = new ArrayList<>();

    /**
     * Adds a pass to the multi pass renderer.
     *
     * @param shader      the shader
     * @param samplerName the sampler/texture
     * @param uniforms    the uniforms
     */
    public void addPass(final Shader shader, final String samplerName, final BiConsumer<Shader, Framebuffer> uniforms) {
        passes.add(new Pass(new FramebufferWrapper(), shader, samplerName, uniforms));
    }

    /**
     * Renders a pass.
     *
     * @param matrices the matrix stack
     * @param shader   the shader
     * @param input    the input buffer
     * @param output   the output buffer
     * @param sampler  the sampler name
     * @param uniforms the uniforms
     */
    private void renderPass(final MatrixStack matrices, final Shader shader, Framebuffer input, Framebuffer output, final String sampler, BiConsumer<Shader, Framebuffer> uniforms) {
        output.beginWrite(true);
        shader.push();
        uniforms.accept(shader, input);
        texture(shader, sampler, input);
        shader.applyShaderVertices(matrices);
        popPasses(shader);
    }

    /**
     * Masks something to render.
     *
     * @param runnable the thing to render
     */
    public void mask(final Runnable runnable) {
        postProcessingBuffer.beginWrite(true);
        runnable.run();
        postProcessingBuffer.endWrite();
        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
    }

    /**
     * Renders the passes.
     *
     * @param stack the matrix stack
     */
    public void render(final MatrixStack stack) {
        this.passes.forEach(pass -> {
            final Framebuffer framebuffer = pass.framebuffer();
            final Shader shader = pass.shader();
            final Framebuffer nextFramebuffer = this.passes.indexOf(pass) == this.passes.size() - 1 ?
                    this.endBuffer :
                    this.passes.get(this.passes.indexOf(pass) + 1).framebuffer();

            this.renderPass(stack, shader, passes.indexOf(pass) == 0 ?
                    MinecraftClient.getInstance().getFramebuffer() :
                    framebuffer, nextFramebuffer, pass.sampler(), pass.uniforms());
        });

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        TRANSPARENT.drawOnScreen(stack, shader -> {
            shader.setUniform("resolution", new Vec2f(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight()));
            this.texture(shader, "inputTexture", postProcessingBuffer);
            this.texture(shader, "outputTexture", endBuffer);
        });

        this.passes.forEach(pass -> pass.framebuffer().clear(MinecraftClient.IS_SYSTEM_MAC));
        this.postProcessingBuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        this.endBuffer.clear(true);

        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);

    }

    private void texture(final Shader shader, final String uniform, final Framebuffer buffer) {
        shader.setUniform(uniform, textures.size());
        RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + textures.size());
        textures.add(GlStateManager._getInteger(GL_TEXTURE_BINDING_2D));
        buffer.beginRead();
    }

    /**
     * Pops the shader & passes.
     *
     * @param shader the shader to pop
     */
    private void popPasses(final Shader shader) {
        for (int i = 0; i < textures.size(); i++) {
            RenderSystem.activeTexture(GlConst.GL_TEXTURE0 + i);
            RenderSystem.bindTexture(textures.get(i));
        }

        textures.clear();
        RenderSystem.activeTexture(GlConst.GL_TEXTURE0);

        shader.pop();
    }

    /**
     * A pass.
     *
     * @param framebuffer the framebuffer
     * @param shader      shader
     * @param sampler     sampler/texture
     * @param uniforms    uniforms
     */
    record Pass(Framebuffer framebuffer, Shader shader, String sampler, BiConsumer<Shader, Framebuffer> uniforms) {
    }

}
