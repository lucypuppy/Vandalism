package de.evilcodez.supermod.render.gl.shader;

import de.evilcodez.supermod.render.gl.buffer.BufferObject;
import de.evilcodez.supermod.render.gl.buffer.BufferTarget;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL45C;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShaderProgram implements AutoCloseable {

    private final int id;
    private final Object2ObjectMap<String, ShaderUniform> uniformCache = new Object2ObjectOpenHashMap<>();

    public static ShaderProgram compose(boolean disposeShaders, Shader... shaders) {
        final int id = GL33C.glCreateProgram();
        try {
            for (Shader shader : shaders) {
                GL33C.glAttachShader(id, shader.id());
            }
            GL33C.glLinkProgram(id);
            if (GL33C.glGetProgrami(id, GL33C.GL_LINK_STATUS) == GL33C.GL_FALSE) {
                GL33C.glDeleteProgram(id);
                throw new ShaderException("Failed to link shader program: " + GL33C.glGetProgramInfoLog(id));
            }
            GL33C.glValidateProgram(id);
            if (GL33C.glGetProgrami(id, GL33C.GL_VALIDATE_STATUS) == GL33C.GL_FALSE) {
                GL33C.glDeleteProgram(id);
                throw new ShaderException("Failed to validate shader program: " + GL33C.glGetProgramInfoLog(id));
            }
        } finally {
            if (disposeShaders) {
                for (Shader shader : shaders) {
                    shader.close();
                }
            }
        }
        return new ShaderProgram(id);
    }

    public static ShaderProgram compose(Shader... shaders) {
        return compose(true, shaders);
    }

    public int id() {
        return this.id;
    }

    public ShaderUniform uniform(String name) {
        return this.uniform(name, true);
    }

    public ShaderUniform uniform(String name, boolean ignoreInvalid) {
        ShaderUniform uniform = this.uniformCache.get(name);
        if (uniform == null) {
            final int id = GL33C.glGetUniformLocation(this.id, name);
            if (id == -1 && !ignoreInvalid) return null;
            uniform = new ShaderUniform(this, id);
            this.uniformCache.put(name, uniform);
        }
        return uniform;
    }

    public int getShaderStorageBlockIndex(String name) {
        return GL45C.glGetProgramResourceIndex(this.id, GL45C.GL_SHADER_STORAGE_BLOCK, name);
    }

    public int getUniformBlockIndex(String name) {
        return GL45C.glGetProgramResourceIndex(this.id, GL45C.GL_UNIFORM_BLOCK, name);
    }

    public void bindUniformBlock(int blockIndex, int bufferBinding) {
        GL33C.glUniformBlockBinding(this.id, blockIndex, bufferBinding);
    }

    public void bindStorageBuffer(int blockIndex, int bufferBinding) {
        GL45C.glShaderStorageBlockBinding(this.id, blockIndex, bufferBinding);
    }

    public void bindUniformBlock(String name, int bufferBinding, BufferObject buffer) {
        this.bindUniformBlock(this.getUniformBlockIndex(name), bufferBinding);
        buffer.bindBase(BufferTarget.UNIFORM_BUFFER, bufferBinding);
    }

    public void bindStorageBuffer(String name, int bufferBinding, BufferObject buffer) {
        this.bindStorageBuffer(this.getShaderStorageBlockIndex(name), bufferBinding);
        buffer.bindBase(BufferTarget.SHADER_STORAGE_BUFFER, bufferBinding);
    }

    public void dispatch(int x, int y, int z) {
        GL45C.glDispatchCompute(x, y, z);
    }

    public void waitForBarriers(int barriers) {
        GL45C.glMemoryBarrier(barriers);
    }

    public void clearCache() {
        this.uniformCache.clear();
    }

    public void bind() {
        GL33C.glUseProgram(this.id);
    }

    public void unbind() {
        GL33C.glUseProgram(0);
    }

    @Override
    public void close() {
        GL33C.glDeleteProgram(this.id);
    }

    public static ShaderProgram byId(int id) {
        return new ShaderProgram(id);
    }

    public static int currentBoundId() {
        return GL33C.glGetInteger(GL33C.GL_CURRENT_PROGRAM);
    }
}
