package de.vandalismdevelopment.vandalism.integration.hud.impl;

import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import de.vandalismdevelopment.vandalism.integration.hud.HUDElement;
import de.vandalismdevelopment.vandalism.util.render.GLStateTracker;
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
        this.mc.getTextureManager().getTexture(FabricBootstrap.MOD_ICON).setFilter(
                true,
                true
        );
        GLStateTracker.BLEND.save(true);
        context.drawTexture(
                FabricBootstrap.MOD_ICON,
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
