package de.evilcodez.supermod.render.shape;

import org.joml.Vector2fc;
import org.joml.Vector3fc;

public interface ShapeVertexConsumer {

    void accept(Vector3fc pos, Vector2fc uv, Vector3fc normal);
}
