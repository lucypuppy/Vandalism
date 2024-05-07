package de.nekosarekawaii.vandalism.render.effect.fill;

import de.nekosarekawaii.vandalism.render.effect.PostProcessEffect;
import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;

public abstract class FillEffect extends PostProcessEffect {

    public FillEffect(String name) {
        super(name);
    }

    protected void addDefaultPass() {
        this.addPass(ctx -> {
            ctx.setShader(this.getShaderPath());
            ctx.setTextureBinding("tex", this.maskFramebuffer());
            ctx.setUniformSetup(this::setupUniforms);
            this.initPass(ctx);
        });
    }

    protected void initPass(PassInitContext ctx) {}

    protected abstract String getShaderPath();

    protected void setupUniforms(ShaderProgram shader) {}
}
