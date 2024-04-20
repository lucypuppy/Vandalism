package de.evilcodez.supermod.render.gl.shader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL45C;

@Getter
@RequiredArgsConstructor
public enum ShaderType {

    VERTEX(GL45C.GL_VERTEX_SHADER, "vertex"),
    FRAGMENT(GL45C.GL_FRAGMENT_SHADER, "fragment"),
    GEOMETRY(GL45C.GL_GEOMETRY_SHADER, "geometry"),
    TESS_CONTROL(GL45C.GL_TESS_CONTROL_SHADER, "tess_control"),
    TESS_EVALUATION(GL45C.GL_TESS_EVALUATION_SHADER, "tess_evaluation"),
    COMPUTE(GL45C.GL_COMPUTE_SHADER, "compute");

    private final int glType;
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
