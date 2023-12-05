package de.vandalismdevelopment.vandalism.gui.ingame.elements;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.gui.ingame.Element;
import de.vandalismdevelopment.vandalism.util.GLStateTracker;
import net.minecraft.client.gui.DrawContext;

public class WatermarkElement extends Element {

    public WatermarkElement() {
        super("Watermark");
    }

    @Override
    protected void resetPosition() {
        super.resetPosition();
        setScreenPosition(0, 0);
    }

    @Override
    public void render(final DrawContext context, final float delta) {
        final int imageWidth = 156, imageHeight = 44;
        this.mc().getTextureManager().getTexture(Vandalism.getInstance().getLogo()).setFilter(true, true);
        GLStateTracker.BLEND.save(true);
        context.drawTexture(Vandalism.getInstance().getLogo(), this.x, this.y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        GLStateTracker.BLEND.revert();
        this.width = imageWidth;
        this.height = imageHeight;
    }

}
