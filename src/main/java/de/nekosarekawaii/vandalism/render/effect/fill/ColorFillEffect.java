package de.nekosarekawaii.vandalism.render.effect.fill;

import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class ColorFillEffect extends FillEffect {

    @Getter
    @Setter
    private Color color = Color.WHITE;

    public ColorFillEffect() {
        super("ColorFill");
        this.addDefaultPass();
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/fill/color_fill";
    }

    @Override
    protected void setupUniforms(ShaderProgram shader) {
        super.setupUniforms(shader);
        shader.uniform("u_FillColor").set(this.color);
    }
}
