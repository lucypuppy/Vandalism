package de.nekosarekawaii.vandalism.render.effect.outline;

public class InnerOutlineEffect extends OutlineEffect {

    public InnerOutlineEffect() {
        super("InnerOutline");
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/outline/inner_outline";
    }

    public void configure(float outlineWidth, float outlineAccuracy) {
        this.setOutlineWidth(outlineWidth);
        this.setOutlineAccuracy(outlineAccuracy);
    }
}
