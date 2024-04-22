package de.nekosarekawaii.vandalism.render.effect.outline;

public class OuterOutlineEffect extends OutlineEffect {

    public OuterOutlineEffect() {
        super("OuterOutline");
    }

    @Override
    protected String getShaderPath() {
        return "postprocess/outline/outer_outline";
    }

    public void configure(float outlineWidth, float outlineAccuracy) {
        this.setOutlineWidth(outlineWidth);
        this.setOutlineAccuracy(outlineAccuracy);
    }
}
