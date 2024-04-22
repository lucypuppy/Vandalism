package de.nekosarekawaii.vandalism.render.effect.outline;

import de.nekosarekawaii.vandalism.render.gl.shader.ShaderProgram;

public class FastOuterOutlineEffect extends OutlineEffect {

    public FastOuterOutlineEffect() {
        super("FastOuterOutline");
        this.addPass(ctx -> { // Adds the second pass to the effect
            ctx.setShader(0);
            ctx.setTextureBinding("mask", this.maskFramebuffer());
            ctx.setTextureBinding("tex", this.framebuffer(0));
            ctx.setUniformSetup(shader -> {
                shader.uniform("u_OutlineWidth").set(this.getOutlineWidth());
                shader.uniform("u_OutlineAccuracy").set(this.getOutlineAccuracy());
                shader.uniform("u_Pass").set(1);
            });
        });
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/outline/fast_outer_outline";
    }

    @Override
    protected void setupAdditionalUniforms(ShaderProgram shader) {
        super.setupAdditionalUniforms(shader);
        shader.uniform("u_Pass").set(0);
    }

    public void configure(float outlineWidth, float outlineAccuracy) {
        this.setOutlineWidth(outlineWidth);
        this.setOutlineAccuracy(outlineAccuracy);
    }
}
