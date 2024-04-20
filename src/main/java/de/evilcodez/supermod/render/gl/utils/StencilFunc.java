package de.evilcodez.supermod.render.gl.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum StencilFunc {

    NEVER(GL45C.GL_NEVER),
    LESS(GL45C.GL_LESS),
    LEQUAL(GL45C.GL_LEQUAL),
    GREATER(GL45C.GL_GREATER),
    GEQUAL(GL45C.GL_GEQUAL),
    EQUAL(GL45C.GL_EQUAL),
    NOTEQUAL(GL45C.GL_NOTEQUAL),
    ALWAYS(GL45C.GL_ALWAYS);

    private final int glType;

    public static StencilFunc byGlType(int glType) {
        for (StencilFunc func : values()) {
            if (func.glType == glType) {
                return func;
            }
        }
        return null;
    }
}
