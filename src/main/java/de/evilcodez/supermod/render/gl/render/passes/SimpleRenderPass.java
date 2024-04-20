package de.evilcodez.supermod.render.gl.render.passes;

import de.evilcodez.supermod.render.gl.render.PrimitiveType;
import de.evilcodez.supermod.render.gl.shader.ShaderProgram;
import de.evilcodez.supermod.render.gl.vertex.VertexLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record SimpleRenderPass(PrimitiveType primitiveType, VertexLayout vertexLayout, @Nullable VertexLayout instanceLayout,
                               Supplier<ShaderProgram> shaderSupplier, Consumer<ShaderProgram> setupShaderUniforms,
                               Supplier<ShaderProgram> instanceShaderSupplier, BiConsumer<ShaderProgram, Integer> setupInstanceShaderUniforms,
                               Runnable setupStateRunnable, Runnable cleanupStateRunnable) implements RenderPass {

    public SimpleRenderPass(PrimitiveType primitiveType, VertexLayout vertexLayout, Supplier<ShaderProgram> shaderSupplier,
                            Runnable setupStateRunnable, Consumer<ShaderProgram> setupUniforms, Runnable cleanupStateRunnable) {
        this(primitiveType, vertexLayout, null, shaderSupplier, setupUniforms, null, null, setupStateRunnable, cleanupStateRunnable);
    }

    public SimpleRenderPass(VertexLayout vertexLayout, Supplier<ShaderProgram> shaderSupplier, Runnable setupStateRunnable,
                            Consumer<ShaderProgram> setupUniforms, Runnable cleanupStateRunnable) {
        this(PrimitiveType.TRIANGLES, vertexLayout, null, shaderSupplier, setupUniforms, null, null, setupStateRunnable, cleanupStateRunnable);
    }

    public SimpleRenderPass(PrimitiveType primitiveType, VertexLayout vertexLayout, Supplier<ShaderProgram> shaderSupplier) {
        this(primitiveType, vertexLayout, null, shaderSupplier, null, null, null, null, null);
    }

    public SimpleRenderPass(VertexLayout vertexLayout, Supplier<ShaderProgram> shaderSupplier) {
        this(PrimitiveType.TRIANGLES, vertexLayout, null, shaderSupplier, null, null, null, null, null);
    }

    public SimpleRenderPass(VertexLayout vertexLayout, VertexLayout instanceLayout, Supplier<ShaderProgram> shaderSupplier, Supplier<ShaderProgram> instanceShaderSupplier) {
        this(PrimitiveType.TRIANGLES, vertexLayout, instanceLayout, shaderSupplier, null, instanceShaderSupplier, null, null, null);
    }

    public SimpleRenderPass(PrimitiveType primitiveType, VertexLayout vertexLayout, VertexLayout instanceLayout, Supplier<ShaderProgram> shaderSupplier, Supplier<ShaderProgram> instanceShaderSupplier) {
        this(primitiveType, vertexLayout, instanceLayout, shaderSupplier, null, instanceShaderSupplier, null, null, null);
    }

    public SimpleRenderPass(PrimitiveType primitiveType, VertexLayout vertexLayout, Supplier<ShaderProgram> shaderSupplier, Consumer<ShaderProgram> setupUniforms) {
        this(primitiveType, vertexLayout, null, shaderSupplier, setupUniforms, null, null, null, null);
    }

    public SimpleRenderPass(VertexLayout vertexLayout, Supplier<ShaderProgram> shaderSupplier, Consumer<ShaderProgram> setupUniforms) {
        this(PrimitiveType.TRIANGLES, vertexLayout, null, shaderSupplier, setupUniforms, null, null, null, null);
    }

    @Override
    public void setupState() {
        if (this.setupStateRunnable != null) this.setupStateRunnable.run();
    }

    @Override
    public void cleanupState() {
        if (this.cleanupStateRunnable != null) this.cleanupStateRunnable.run();
    }

    @Override
    public void setupUniforms(ShaderProgram program, int instanceCount) {
        if (instanceCount == -1) {
            if (this.setupShaderUniforms != null) this.setupShaderUniforms.accept(program);
        } else {
            if (this.setupInstanceShaderUniforms != null) this.setupInstanceShaderUniforms.accept(program, instanceCount);
            else if (this.setupShaderUniforms != null) this.setupShaderUniforms.accept(program);
        }
    }

    @Override
    public ShaderProgram getShaderProgram() {
        return this.shaderSupplier != null ? this.shaderSupplier.get() : null;
    }

    @Override
    public ShaderProgram getInstancedShaderProgram() {
        return this.instanceShaderSupplier != null ? this.instanceShaderSupplier.get() : null;
    }

    @Override
    public @NotNull VertexLayout getVertexLayout() {
        return this.vertexLayout;
    }

    @Override
    public @Nullable VertexLayout getInstanceLayout() {
        return this.instanceLayout;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return this.primitiveType;
    }
}
