package de.vandalismdevelopment.vandalism.integration.hud.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.integration.hud.HUDElement;
import de.vandalismdevelopment.vandalism.util.GLStateTracker;
import net.minecraft.client.gui.DrawContext;

public class WatermarkHUDElement extends HUDElement {

    public WatermarkHUDElement() {
        super(
                "Watermark",
                0,
                0
        );
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        final int imageWidth = 156, imageHeight = 44;
        this.mc().getTextureManager().getTexture(Vandalism.getInstance().getLogo()).setFilter(
                true,
                true
        );
        GLStateTracker.BLEND.save(true);
        context.drawTexture(
                Vandalism.getInstance().getLogo(),
                this.x,
                this.y,
                0,
                0,
                imageWidth,
                imageHeight,
                imageWidth,
                imageHeight
        );
        GLStateTracker.BLEND.revert();
        this.width = imageWidth;
        this.height = imageHeight;
    }

}
