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
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.tooltip.ConvertibleTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

public class MapTooltipComponent implements TooltipComponent, MinecraftWrapper, ConvertibleTooltipData {

    private static final Identifier TEXTURE_MAP_BACKGROUND = Identifier.of("textures/map/map_background.png");
    private final MapIdComponent mapId;
    private final float scale = 1.0f;

    public MapTooltipComponent(final MapIdComponent mapId) {
        this.mapId = mapId;
    }

    @Override
    public int getHeight() {
        return (int) ((128 + 16) * this.scale) + 2;
    }

    @Override
    public int getWidth(final TextRenderer textRenderer) {
        return (int) ((128 + 16) * this.scale);
    }

    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(this.scale * 2, this.scale * 2, 0);
        matrices.scale((64 + 8) / 64f, (64 + 8) / 64f, 0);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE_MAP_BACKGROUND);
        context.drawTexture(TEXTURE_MAP_BACKGROUND, 0, 0, 0, 0, 0, 64, 64, 64, 64);
        matrices.pop();
        final VertexConsumerProvider.Immediate consumer = mc.getBufferBuilders().getEntityVertexConsumers();
        final MapState mapState = FilledMapItem.getMapState(this.mapId, mc.world);
        if (mapState == null) {
            return;
        }
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(this.scale, this.scale, 0);
        matrices.translate(8, 8, 0);
        mc.gameRenderer.getMapRenderer().draw(matrices, consumer, this.mapId, mapState, false, 0xF000F0);
        consumer.draw();
        matrices.pop();
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

}