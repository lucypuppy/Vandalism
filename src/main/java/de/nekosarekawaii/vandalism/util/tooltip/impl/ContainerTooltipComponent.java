/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.tooltip.ConvertibleTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class ContainerTooltipComponent implements TooltipComponent, MinecraftWrapper, ConvertibleTooltipData {

    private static final Identifier TEXTURE_CONTAINER_BACKGROUND = Identifier.of(FabricBootstrap.MOD_ID, "textures/hud/container.png");

    private static final int WIDTH = 176, HEIGHT = 67;

    private final DefaultedList<ItemStack> items;
    private final Color color;

    public ContainerTooltipComponent(final DefaultedList<ItemStack> items, final Color color) {
        this.items = items;
        this.color = color;
    }

    @Override
    public int getWidth(final TextRenderer textRenderer) {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
        final MatrixStack matrixStack = context.getMatrices();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f, this.color.getAlpha() / 255f);
        context.drawTexture(TEXTURE_CONTAINER_BACKGROUND, x, y, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        int row = 0, column = 0;
        for (final ItemStack itemStack : this.items) {
            matrixStack.push();
            matrixStack.translate(0, 0, 401);
            context.drawItem(itemStack, x + 8 + column * 18, y + 7 + row * 18);
            context.drawItemInSlot(textRenderer, itemStack, x + 8 + column * 18, y + 7 + row * 18, null);
            matrixStack.pop();

            column++;
            if (column >= 9) {
                column = 0;
                row++;
            }
        }
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

}