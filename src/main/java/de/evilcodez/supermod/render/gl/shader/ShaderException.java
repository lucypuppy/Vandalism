package de.evilcodez.supermod.render.gl.shader;

public class ShaderException extends RuntimeException {

    public ShaderException(String message) {
        super(message);
    }

    public ShaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderException(Throwable cause) {
        super(cause);
    }
}
