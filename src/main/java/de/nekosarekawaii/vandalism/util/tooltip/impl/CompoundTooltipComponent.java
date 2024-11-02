/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.util.tooltip.impl;

import com.google.common.collect.Lists;
import de.nekosarekawaii.vandalism.util.tooltip.ConvertibleTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;

public class CompoundTooltipComponent implements TooltipComponent, ConvertibleTooltipData {

    private final List<TooltipComponent> components = Lists.newArrayList();

    public void addComponent(final TooltipComponent component) {
        components.add(component);
    }

    @Override
    public int getHeight() {
        int height = 0;
        for (final TooltipComponent comp : this.components) {
            height += comp.getHeight();
        }
        return height;
    }

    @Override
    public int getWidth(final TextRenderer textRenderer) {
        int width = 0;
        for (final TooltipComponent comp : this.components) {
            if (comp.getWidth(textRenderer) > width) {
                width = comp.getWidth(textRenderer);
            }
        }
        return width;
    }

    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
        int yOff = 0;
        for (final TooltipComponent comp : this.components) {
            comp.drawItems(textRenderer, x, y + yOff, context);
            yOff += comp.getHeight();
        }
    }

    @Override
    public void drawText(final TextRenderer textRenderer, final int x, final int y, final Matrix4f matrix4f, final VertexConsumerProvider.Immediate immediate) {
        int yOff = 0;
        for (final TooltipComponent comp : this.components) {
            comp.drawText(textRenderer, x, y + yOff, matrix4f, immediate);
            yOff += comp.getHeight();
        }
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

}