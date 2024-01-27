/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
            "The color of the watermark.",
            Color.WHITE
    );

    public WatermarkHUDElement() {
        super(
                "Watermark",
                2,
                4
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
                selectedColor.getRed() / 255f,
                selectedColor.getGreen() / 255f,
                selectedColor.getBlue() / 255f,
                selectedColor.getAlpha() / 255f
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
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLStateTracker.BLEND.revert();
        this.width = IMAGE_WIDTH;
        this.height = IMAGE_HEIGHT;
    }

}
