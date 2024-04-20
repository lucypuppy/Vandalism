package de.evilcodez.supermod.render.gl.render.passes;

import de.evilcodez.supermod.render.gl.render.PrimitiveType;
import de.evilcodez.supermod.render.gl.shader.ShaderProgram;
import de.evilcodez.supermod.render.gl.vertex.VertexLayout;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString
@RequiredArgsConstructor
public class UniqueRenderPass implements RenderPass {

    private final RenderPass pass;

    @Override
    public void setupState() {
        this.pass.setupState();
    }

    @Override
    public void cleanupState() {
        this.pass.cleanupState();
    }

    @Override
    public void setupUniforms(ShaderProgram program, int instanceCount) {
        this.pass.setupUniforms(program, instanceCount);
    }

    @Override
    public ShaderProgram getShaderProgram() {
        return this.pass.getShaderProgram();
    }

    @Override
    public ShaderProgram getInstancedShaderProgram() {
        return this.pass.getInstancedShaderProgram();
    }

    @Override
    public @NotNull VertexLayout getVertexLayout() {
        return this.pass.getVertexLayout();
    }

    @Override
    public @Nullable VertexLayout getInstanceLayout() {
        return this.pass.getInstanceLayout();
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return this.pass.getPrimitiveType();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
