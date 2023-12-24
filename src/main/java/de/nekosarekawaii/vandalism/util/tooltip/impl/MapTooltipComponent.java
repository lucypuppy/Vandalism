package de.nekosarekawaii.vandalism.util.tooltip.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.tooltip.ConvertibleTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

public class MapTooltipComponent implements TooltipComponent, MinecraftWrapper, ConvertibleTooltipData {

    private static final Identifier TEXTURE_MAP_BACKGROUND = new Identifier("textures/map/map_background.png");
    private final int mapId;
    private final float scale = 1.0f;

    public MapTooltipComponent(final int mapId) {
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
        final VertexConsumerProvider.Immediate consumer = this.mc.getBufferBuilders().getEntityVertexConsumers();
        final MapState mapState = FilledMapItem.getMapState(this.mapId, this.mc.world);
        if (mapState == null) {
            return;
        }
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(this.scale, this.scale, 0);
        matrices.translate(8, 8, 0);
        this.mc.gameRenderer.getMapRenderer().draw(matrices, consumer, this.mapId, mapState, false, 0xF000F0);
        consumer.draw();
        matrices.pop();
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

}