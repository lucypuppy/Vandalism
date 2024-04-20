package de.evilcodez.supermod.render.gl.buffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum BufferAccess {
    READ_ONLY(GL45C.GL_READ_ONLY),
    WRITE_ONLY(GL45C.GL_WRITE_ONLY),
    READ_WRITE(GL45C.GL_READ_WRITE);

    private final int glType;
}
