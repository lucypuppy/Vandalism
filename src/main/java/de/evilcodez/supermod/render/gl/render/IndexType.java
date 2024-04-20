package de.evilcodez.supermod.render.gl.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum IndexType {

    UNSIGNED_BYTE(GL45C.GL_UNSIGNED_BYTE, 1),
    UNSIGNED_SHORT(GL45C.GL_UNSIGNED_SHORT, 2),
    UNSIGNED_INT(GL45C.GL_UNSIGNED_INT, 4);

    private final int glType;
    private final int byteSize;
}
