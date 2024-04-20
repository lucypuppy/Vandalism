package de.evilcodez.supermod.render.gl.shader;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL33C;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Shader implements AutoCloseable {

    private final ShaderType type;
    private final int id;

    public static Shader compile(ShaderType type, String source) {
        final int id = GL33C.glCreateShader(type.getGlType());
        GL33C.glShaderSource(id, source);
        GL33C.glCompileShader(id);
        final int status = GL33C.glGetShaderi(id, GL33C.GL_COMPILE_STATUS);
        if (status == GL33C.GL_FALSE) {
            final String log = GL33C.glGetShaderInfoLog(id);
            GL33C.glDeleteShader(id);
            throw new ShaderException("Failed to compile shader: " + log);
        }
        return new Shader(type, id);
    }

    public ShaderType type() {
        return this.type;
    }

    public int id() {
        return this.id;
    }

    @Override
    public void close() {
        GL33C.glDeleteShader(this.id);
    }
}
