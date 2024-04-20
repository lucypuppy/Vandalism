package de.evilcodez.supermod.render.gl.vertex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum DrawMode {

    POINTS("Points", GL45C.GL_POINTS),
    LINE_STRIP("Line-Strip", GL45C.GL_LINE_STRIP),
    LINE_LOOP("Line-Loop", GL45C.GL_LINE_LOOP),
    LINES("Lines", GL45C.GL_LINES),
    LINE_STRIP_ADJACENCY("Line-Strip-Adjacency", GL45C.GL_LINE_STRIP_ADJACENCY),
    TRIANGLE_STRIP("Triangle-Strip", GL45C.GL_TRIANGLE_STRIP),
    TRIANGLE_FAN("Triangle-Fan", GL45C.GL_TRIANGLE_FAN),
    TRIANGLES("Triangles", GL45C.GL_TRIANGLES),
    TRIANGLE_STRIP_ADJACENCY("Triangle-Strip-Adjacency", GL45C.GL_TRIANGLE_STRIP_ADJACENCY),
    TRIANGLES_ADJACENCY("Triangles-Adjacency", GL45C.GL_TRIANGLES_ADJACENCY),
    PATCHES("Patches", GL45C.GL_PATCHES);

    private final String name;
    private final int glType;

    @Override
    public String toString() {
        return name;
    }

    public static DrawMode byGlType(int glType) {
        for (DrawMode mode : values()) {
            if (mode.glType == glType) {
                return mode;
            }
        }
        return null;
    }
}
