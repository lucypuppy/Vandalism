/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.util.render.tooltip.impl;

import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.tooltip.ConvertibleTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;

public class BannerTooltipComponent implements TooltipComponent, ConvertibleTooltipData, MinecraftWrapper {

    private final ItemStack banner;
    private final ModelPart bannerField;

    public BannerTooltipComponent(final ItemStack banner) {
        this.banner = banner;
        this.bannerField = this.mc.getEntityModelLoader().getModelPart(EntityModelLayers.BANNER).getChild("flag");
    }

    @Override
    public int getHeight() {
        return 32 * 5 - 2;
    }

    @Override
    public int getWidth(final TextRenderer textRenderer) {
        return 16 * 5;
    }

    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
        DiffuseLighting.disableGuiDepthLighting();
        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x + 8, y + 8, 0);
        matrices.push();
        matrices.translate(0.5, 16, 0);
        matrices.scale(6, -6, 1);
        matrices.scale(2, -2, -2);
        matrices.push();
        matrices.translate(2.5, 8.5, 0);
        matrices.scale(5, 5, 5);
        final VertexConsumerProvider.Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
        this.bannerField.pitch = 0f;
        this.bannerField.pivotY = -32f;
        final BannerItem bannerItem = (BannerItem) this.banner.getItem();
        BannerBlockEntityRenderer.renderCanvas(
                matrices,
                immediate,
                0xF000F0,
                OverlayTexture.DEFAULT_UV,
                this.bannerField,
                ModelLoader.BANNER_BASE,
                true,
                bannerItem.getColor(),
                this.banner.get(DataComponentTypes.BANNER_PATTERNS)
        );
        matrices.pop();
        matrices.pop();
        immediate.draw();
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

}