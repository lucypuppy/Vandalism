package de.nekosarekawaii.vandalism.integration.hud.impl;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.render.GLStateTracker;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class WatermarkHUDElement extends HUDElement {

    private ColorValue color = new ColorValue(this, "Color", "Define the color of the water mark", Color.WHITE);

    public WatermarkHUDElement() {
        super(
                "Watermark",
                2,
                2
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
        context.setShaderColor(color.getValue().getRed() / 255f, color.getValue().getGreen() / 255f,
                color.getValue().getBlue() / 255f, color.getValue().getAlpha() / 255f);
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
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GLStateTracker.BLEND.revert();
        this.width = imageWidth;
        this.height = imageHeight;
    }

}
