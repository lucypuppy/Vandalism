package de.evilcodez.supermod.render.gl.buffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum BufferUsage {

    STREAM_DRAW(GL45C.GL_STREAM_DRAW),
    STREAM_READ(GL45C.GL_STREAM_READ),
    STREAM_COPY(GL45C.GL_STREAM_COPY),
    STATIC_DRAW(GL45C.GL_STATIC_DRAW),
    STATIC_READ(GL45C.GL_STATIC_READ),
    STATIC_COPY(GL45C.GL_STATIC_COPY),
    DYNAMIC_DRAW(GL45C.GL_DYNAMIC_DRAW),
    DYNAMIC_READ(GL45C.GL_DYNAMIC_READ),
    DYNAMIC_COPY(GL45C.GL_DYNAMIC_COPY);

    private final int glType;

    public static BufferUsage byGlType(int glType) {
        for (BufferUsage usage : values()) {
            if (usage.glType == glType) {
                return usage;
            }
        }
        return null;
    }
}
