package de.nekosarekawaii.vandalism.integration.hud.impl;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.render.GLStateTracker;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class WatermarkHUDElement extends HUDElement {

    private static final int IMAGE_WIDTH = 156, IMAGE_HEIGHT = 44;

    private final ColorValue color = new ColorValue(
            this,
            "Color",
            "Define the color of the water mark",
            Color.WHITE
    );

    public WatermarkHUDElement() {
        super(
                "Watermark",
                2,
                2
        );
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        this.mc.getTextureManager().getTexture(FabricBootstrap.MOD_ICON).setFilter(
                true,
                true
        );
        GLStateTracker.BLEND.save(true);

        final Color selectedColor = this.color.getValue().getColor();
        context.setShaderColor(
                selectedColor.getRed() / 255F,
                selectedColor.getGreen() / 255F,
                selectedColor.getBlue() / 255F,
                selectedColor.getAlpha() / 255F
        );
        context.drawTexture(
                FabricBootstrap.MOD_ICON,
                this.x,
                this.y,
                0,
                0,
                IMAGE_WIDTH,
                IMAGE_HEIGHT,
                IMAGE_WIDTH,
                IMAGE_HEIGHT
        );
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GLStateTracker.BLEND.revert();
        this.width = IMAGE_WIDTH;
        this.height = IMAGE_HEIGHT;
    }

}
