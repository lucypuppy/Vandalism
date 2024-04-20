package de.evilcodez.supermod.render.gl.render.passes;

import de.evilcodez.supermod.render.gl.render.PrimitiveType;
import de.evilcodez.supermod.render.gl.shader.ShaderProgram;
import de.evilcodez.supermod.render.gl.vertex.VertexLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderPass {

    void setupState();

    void cleanupState();

    /**
     * Set up the uniforms for the given program.
     * @param program The program to set up the uniforms for.
     * @param instanceCount The number of instances to render. -1 if not instanced.
     */
    void setupUniforms(ShaderProgram program, int instanceCount);

    ShaderProgram getShaderProgram();

    default ShaderProgram getInstancedShaderProgram() {
        return null;
    }

    /**
     * @return The layout of the vertex attributes.
     */
    @NotNull
    VertexLayout getVertexLayout();

    @Nullable
    default VertexLayout getInstanceLayout() {
        return null;
    }

    default PrimitiveType getPrimitiveType() {
        return PrimitiveType.TRIANGLES;
    }
}
