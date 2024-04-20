package de.evilcodez.supermod.render.gl.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum PrimitiveType {

    TRIANGLES(GL45C.GL_TRIANGLES, false),
    TRIANGLE_STRIP(GL45C.GL_TRIANGLE_STRIP, true),
    TRIANGLE_FAN(GL45C.GL_TRIANGLE_FAN, true),
    QUADS(GL45C.GL_TRIANGLES, false),
    POINTS(GL45C.GL_POINTS, false),
    GL_LINES(GL45C.GL_LINES, false),
    GL_LINE_STRIP(GL45C.GL_LINE_STRIP, true),
    GL_LINE_LOOP(GL45C.GL_LINE_LOOP, true),
    MINECRAFT_LINES(GL45C.GL_TRIANGLES, false),
    MINECRAFT_LINE_STRIP(GL45C.GL_TRIANGLE_STRIP, true);

    private final int glType;
    private final boolean connectedPrimitive;

    public static PrimitiveType byGlType(int glType) {
        for (PrimitiveType type : values()) {
            if (type.glType == glType) {
                return type;
            }
        }
        return null;
    }
}
