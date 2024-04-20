package de.evilcodez.supermod.render.gl.render;

import de.evilcodez.supermod.render.gl.vertex.VertexLayout;
import org.jetbrains.annotations.NotNull;

public interface AttribConsumerSet {

    /**
     * @return The consumer for the main vertex array.
     */
    @NotNull
    InstancedAttribConsumer main();

    @NotNull
    IndexConsumer indexData(IndexType type);

    @NotNull
    default IndexConsumer indexData() {
        return this.indexData(IndexType.UNSIGNED_INT);
    }

    /**
     * Gets or creates a custom vertex array identified by the given data layout.
     * @param layout The layout of the custom vertex array.
     * @return The consumer for the custom vertex array.
     */
    @NotNull
    AttribConsumer custom(@NotNull VertexLayout layout);
}
