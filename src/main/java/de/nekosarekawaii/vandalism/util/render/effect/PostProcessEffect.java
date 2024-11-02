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

package de.nekosarekawaii.vandalism.util.render.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderType;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL45C;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class PostProcessEffect implements AutoCloseable {

    @Getter
    private final String name;
    private boolean initialized;
    private final List<Consumer<PassInitContext>> passInitializers = new ArrayList<>();
    private int numPasses;
    private ObjectArrayList<SimpleFramebuffer> maskStack;
    private int maskStackIndex;
    private SimpleFramebuffer[] framebuffers; // Size is one less than numPasses because the last pass renders into the output framebuffer
    private Object2ObjectMap<String, IntSupplier>[] textureBindings;
    private ShaderProgram[] shaders;
    private Consumer<ShaderProgram>[] uniformSetups;
    private double startTime;
    @Getter
    @Setter
    private boolean noClear;

    protected final void addPass(Consumer<PassInitContext> pass) {
        if (this.initialized) throw new IllegalStateException("Effect already initialized");
        this.passInitializers.add(pass);
    }

    protected final void addPassThroughPass(Supplier<? extends Framebuffer> framebuffer) {
        if (this.initialized) throw new IllegalStateException("Effect already initialized");
        this.passInitializers.add(ctx -> {
            ctx.setShader("postprocess/passthrough");
            ctx.setTextureBinding("tex", framebuffer);
        });
    }

    public final void clearPasses() {
        if (this.initialized) throw new IllegalStateException("Effect already initialized");
        this.passInitializers.clear();
    }

    public final void initialize() {
        if (this.initialized) throw new IllegalStateException("Effect already initialized");
        if (this.passInitializers.isEmpty()) throw new IllegalStateException("No passes added");
        this.numPasses = this.passInitializers.size();
        this.framebuffers = new SimpleFramebuffer[this.numPasses];
        this.shaders = new ShaderProgram[this.numPasses];
        this.uniformSetups = new Consumer[this.numPasses];
        this.textureBindings = new Object2ObjectMap[this.numPasses];
        final PassInitContext[] passInitContexts = new PassInitContext[this.numPasses];
        final MinecraftClient mc = MinecraftClient.getInstance();
        Buffers.saveBuffer();
        try {
            this.maskStack = new ObjectArrayList<>();
            this.maskStack.push(new SimpleFramebuffer(mc.getWindow().getWidth(), mc.getWindow().getHeight(), true, MinecraftClient.IS_SYSTEM_MAC));
            this.maskStackIndex = 0;
            for (int i = 0; i < this.passInitializers.size(); i++) {
                passInitContexts[i] = new PassInitContext(i);
                this.framebuffers[i] = new SimpleFramebuffer(mc.getWindow().getWidth(), mc.getWindow().getHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
                this.passInitializers.get(i).accept(passInitContexts[i]);
                this.textureBindings[i] = passInitContexts[i].textureBindings;
            }
            this.passInitializers.clear();
            for (int i = 0; i < this.numPasses; i++) {
                if (passInitContexts[i].shader != null) {
                    this.shaders[i] = passInitContexts[i].shader;
                }
                this.uniformSetups[i] = passInitContexts[i].uniformSetup;
            }
            for (int i = 0; i < this.numPasses; i++) {
                if (passInitContexts[i].shaderIndex != -1) {
                    if (passInitContexts[i].shaderIndex >= this.numPasses) throw new IllegalStateException("Invalid shader index: " + passInitContexts[i].shaderIndex + " (must be < " + this.numPasses + ")");
                    this.shaders[i] = this.shaders[passInitContexts[i].shaderIndex];
                }
                if (this.shaders[i] == null) throw new IllegalStateException("No shader set for pass " + i);
            }
            this.startTime = GLFW.glfwGetTime();
            this.initialized = true;
        } catch (Throwable e) { // Cleanup if an exception occurred
            for (SimpleFramebuffer mask : this.maskStack) {
                mask.delete();
            }
            this.maskStack.clear();
            this.maskStack = null;
            this.maskStackIndex = 0;
            for (int i = 0; i < this.numPasses; i++) { // Delete shaders that were already created
                if (passInitContexts[i] == null) break;
                if (this.framebuffers[i] != null) this.framebuffers[i].delete();
                if (passInitContexts[i].shader != null) {
                    passInitContexts[i].shader.close();
                }
            }
            throw e;
        } finally {
            Buffers.restoreBuffer();
        }
    }

    public final void resizeBuffers(int width, int height) {
        if (!this.initialized) throw new IllegalStateException("Effect not initialized");
        for (SimpleFramebuffer mask : this.maskStack) {
            mask.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }
        for (int i = 0; i < this.numPasses; i++) {
            this.framebuffers[i].resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    protected void setSharedUniforms(ShaderProgram shader, int x, int y, int width, int height) {
        GlobalUniforms.setGlobalUniforms(shader, false);
        shader.uniform("u_Time").set((float) (GLFW.glfwGetTime() - this.startTime)); // Overwrite time uniform from GlobalUniforms
        shader.uniform("u_ShaderBounds").set((float) x, (float) y, (float) x + (float) width, (float) y + (float) height);
        shader.uniform("u_Resolution").set(width, height);
    }

    public void reset() {}

    public void pushMask() {
        if (this.maskStackIndex + 1 >= this.maskStack.size()) {
            this.maskStack.push(new SimpleFramebuffer(this.maskStack.get(this.maskStackIndex).textureWidth, this.maskStack.get(this.maskStackIndex).textureHeight, true, MinecraftClient.IS_SYSTEM_MAC));
        }
        ++this.maskStackIndex;
    }

    public void popMask() {
        if (this.maskStackIndex <= 0) return;
        --this.maskStackIndex;
    }

    public void bindMask() {
        this.maskStack.get(this.maskStackIndex).beginWrite(false);
    }

    private void runPass(int passIndex, Framebuffer framebuffer,
                         int boundX, int boundY, int boundWidth, int boundHeight,
                         float rectLeft, float rectTop, float rectRight, float rectBottom) {
        final ShaderProgram shader = this.shaders[passIndex];
        framebuffer.beginWrite(false);
        shader.bind();
        this.setSharedUniforms(shader, boundX, boundY, boundWidth, boundHeight);
        if (this.uniformSetups[passIndex] != null) {
            this.uniformSetups[passIndex].accept(shader);
        }
        int textureUnit = 0;
        for (Object2ObjectMap.Entry<String, IntSupplier> entry : this.textureBindings[passIndex].object2ObjectEntrySet()) {
            RenderSystem.activeTexture(GL45C.GL_TEXTURE0 + textureUnit);
            RenderSystem.bindTexture(entry.getValue().getAsInt());
            shader.uniform(entry.getKey()).set(textureUnit++);
        }
        RenderSystem.activeTexture(GL45C.GL_TEXTURE0);
        this.drawRect(rectLeft, rectTop, rectRight, rectBottom);
    }

    private void runPassThroughPass(int passThroughTextureId, Framebuffer framebuffer, int boundX, int boundY, int boundWidth, int boundHeight,
                                    float rectLeft, float rectTop, float rectRight, float rectBottom) {
        final ShaderProgram shader = Shaders.getPassThroughShader();
        framebuffer.beginWrite(false);
        shader.bind();
        this.setSharedUniforms(shader, boundX, boundY, boundWidth, boundHeight);
        RenderSystem.activeTexture(GL45C.GL_TEXTURE0);
        RenderSystem.bindTexture(passThroughTextureId);
        shader.uniform("tex").set(0);
        this.drawRect(rectLeft, rectTop, rectRight, rectBottom);
    }

    private void drawRect(float rectLeft, float rectTop, float rectRight, float rectBottom) {
        final BufferBuilder bb = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bb.vertex(rectLeft, rectBottom, 0.0f);
        bb.vertex(rectRight, rectBottom, 0.0f);
        bb.vertex(rectRight, rectTop, 0.0f);
        bb.vertex(rectLeft, rectTop, 0.0f);
        RenderSystem.enableBlend();
        BufferRenderer.draw(bb.end());
        RenderSystem.disableBlend();
    }

    private void renderInternal(MinecraftClient mc, Framebuffer framebuffer, boolean setShaderBounds, boolean renderPassThrough, int x, int y, int width, int height) {
        final float guiWidth = mc.getWindow().getWidth();
        final float guiHeight = mc.getWindow().getHeight();
        float rectWidth = width / guiWidth * 2.0f;
        float rectHeight = height / guiHeight * 2.0f;
        float rectLeft = x / guiWidth * 2.0f - 1.0f;
        float rectTop = -1.0f * (y / guiHeight * 2.0f - 1.0f);
        if (!setShaderBounds) {
            x = 0;
            y = 0;
            width = mc.getWindow().getWidth();
            height = mc.getWindow().getHeight();
        }
        for (int i = 0; i < this.numPasses; i++) {
            this.runPass(i, i == this.numPasses - 1 && !renderPassThrough ? framebuffer : this.framebuffers[i],
                    x, y, width, height, rectLeft, rectTop, rectLeft + rectWidth, rectTop - rectHeight);
        }
        if (renderPassThrough) {
            this.runPassThroughPass(this.framebuffers[this.numPasses - 1].getColorAttachment(), framebuffer, x, y, width, height, rectLeft, rectTop, rectLeft + rectWidth, rectTop - rectHeight);
        }

        if (!this.noClear) this.clearBuffers();
    }

    public final void renderFullscreen(Framebuffer framebuffer, boolean renderPassThrough) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        this.renderInternal(mc, framebuffer, false, renderPassThrough, 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
    }

    public final void renderScissored(Framebuffer framebuffer, boolean renderPassThrough, int x, int y, int width, int height) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        this.renderInternal(mc, framebuffer, false, renderPassThrough, x, y, width, height);
    }

    public final void renderScissoredScaled(Framebuffer framebuffer, boolean renderPassThrough, int x, int y, int width, int height) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final double scale = mc.getWindow().getScaleFactor();
        this.renderInternal(mc, framebuffer, false, renderPassThrough, (int) (x * scale), (int) (y * scale), (int) (width * scale), (int) (height * scale));
    }

    public final void renderRect(Framebuffer framebuffer, boolean renderPassThrough, int x, int y, int width, int height) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        this.renderInternal(mc, framebuffer, true, renderPassThrough, x, y, width, height);
    }

    public final void renderRectScaled(Framebuffer framebuffer, boolean renderPassThrough, int x, int y, int width, int height) {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final double scale = mc.getWindow().getScaleFactor();
        this.renderInternal(mc, framebuffer, true, renderPassThrough, (int) (x * scale), (int) (y * scale), (int) (width * scale), (int) (height * scale));
    }

    public final void clearBuffers() {
        Buffers.saveBuffer();
        for (SimpleFramebuffer mask : this.maskStack) {
            mask.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            mask.clear(MinecraftClient.IS_SYSTEM_MAC);
        }
        for (int i = 0; i < this.numPasses; i++) {
            this.framebuffers[i].setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            this.framebuffers[i].clear(MinecraftClient.IS_SYSTEM_MAC);
        }
        Buffers.restoreBuffer();
        this.reset();
    }

    public final void renderFramebuffer(SimpleFramebuffer fromBuffer, Framebuffer toBuffer) {
        this.runPassThroughPass(fromBuffer.getColorAttachment(), toBuffer, 0, 0, toBuffer.textureWidth, toBuffer.textureHeight, -1.0f, 1.0f, 1.0f, -1.0f);
    }

    @Override
    public final void close() {
        if (!this.initialized) return;
        Buffers.saveBuffer();
        for (SimpleFramebuffer mask : this.maskStack) {
            mask.delete();
        }
        this.maskStack.clear();
        this.maskStack = null;
        this.maskStackIndex = 0;
        for (int i = 0; i < this.numPasses; i++) {
            this.framebuffers[i].delete();
            this.framebuffers[i] = null;
            if (this.shaders[i] != null) {
                this.shaders[i].close();
                this.shaders[i] = null;
            }
            this.uniformSetups[i] = null;
        }
        Buffers.restoreBuffer();
    }

    public final int numPasses() {
        return this.numPasses;
    }

    public final Supplier<SimpleFramebuffer> maskFramebuffer() {
        return () -> this.maskStack.get(this.maskStackIndex);
    }

    public final SimpleFramebuffer getFramebuffer(int pass) {
        return this.framebuffers[pass];
    }

    public final Supplier<SimpleFramebuffer> framebuffer(int pass) {
        return () -> this.framebuffers[pass];
    }

    public final ShaderProgram shader(int pass) {
        return this.shaders[pass];
    }

    public final Consumer<ShaderProgram> uniformSetup(int pass) {
        return this.uniformSetups[pass];
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    protected final class PassInitContext {

        private final int passIndex;
        private final Object2ObjectMap<String, IntSupplier> textureBindings = new Object2ObjectOpenHashMap<>();
        private ShaderProgram shader;
        private int shaderIndex = -1;
        private Consumer<ShaderProgram> uniformSetup;

        public PassInitContext setShader(String fragmentShaderPath) {
            if (this.shader != null || this.shaderIndex != -1) throw new IllegalStateException("Shader already set");
            this.shader = Shaders.create(
                    PostProcessEffect.this.name + "_pass-" + this.passIndex,
                    Shaders.load(ShaderType.VERTEX, "postprocess/postprocess"),
                    Shaders.load(ShaderType.FRAGMENT, fragmentShaderPath)
            );
            return this;
        }

        public PassInitContext setShader(String fragmentShaderPath, String geometryShaderPath) {
            if (this.shader != null || this.shaderIndex != -1) throw new IllegalStateException("Shader already set");
            this.shader = Shaders.create(
                    PostProcessEffect.this.name + "_pass-" + this.passIndex,
                    Shaders.load(ShaderType.VERTEX, "postprocess/postprocess.vert"),
                    Shaders.load(ShaderType.GEOMETRY, geometryShaderPath),
                    Shaders.load(ShaderType.FRAGMENT, fragmentShaderPath)
            );
            return this;
        }

        /** Re-use a shader from another pass. */
        public PassInitContext setShader(int shaderIndex) {
            if (this.shader != null || this.shaderIndex != -1) throw new IllegalStateException("Shader already set");
            if (shaderIndex < 0) throw new IllegalArgumentException("Invalid shader index: " + shaderIndex + " (must be >= 0)");
            this.shaderIndex = shaderIndex;
            return this;
        }

        public PassInitContext setUniformSetup(Consumer<ShaderProgram> uniformSetup) {
            if (this.uniformSetup != null) throw new IllegalStateException("Uniform setup already set");
            this.uniformSetup = uniformSetup;
            return this;
        }

        public PassInitContext setTextureBinding(String uniformName, Framebuffer frameBuffer) {
            this.textureBindings.put(uniformName, frameBuffer::getColorAttachment);
            return this;
        }

        public PassInitContext setTextureBinding(String uniformName, Supplier<? extends Framebuffer> frameBuffer) {
            this.textureBindings.put(uniformName, () -> frameBuffer.get().getColorAttachment());
            return this;
        }

        public PassInitContext setTextureBinding(String uniformName, int textureId) {
            this.textureBindings.put(uniformName, () -> textureId);
            return this;
        }

        public PassInitContext setTextureBinding(String uniformName, IntSupplier textureIdSupplier) {
            this.textureBindings.put(uniformName, textureIdSupplier);
            return this;
        }

        public PassInitContext setTextureBinding(String uniformName, AbstractTexture texture) {
            this.textureBindings.put(uniformName, texture::getGlId);
            return this;
        }
    }
}
