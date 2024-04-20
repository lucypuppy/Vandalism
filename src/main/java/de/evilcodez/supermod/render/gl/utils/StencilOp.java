package de.evilcodez.supermod.render.gl.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum StencilOp {

    KEEP(GL45C.GL_KEEP),
    ZERO(GL45C.GL_ZERO),
    REPLACE(GL45C.GL_REPLACE),
    INCR(GL45C.GL_INCR),
    INCR_WRAP(GL45C.GL_INCR_WRAP),
    DECR(GL45C.GL_DECR),
    DECR_WRAP(GL45C.GL_DECR_WRAP),
    INVERT(GL45C.GL_INVERT);

    private final int glType;

    public static StencilOp byGlType(int glType) {
        for (StencilOp op : values()) {
            if (op.glType == glType) {
                return op;
            }
        }
        return null;
    }
}
