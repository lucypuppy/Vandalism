package de.vandalismdevelopment.vandalism.util.minecraft.impl.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class ContainerTooltipComponent implements TooltipComponent, ITooltipData, MinecraftWrapper {

    private static final Identifier TEXTURE_CONTAINER_BACKGROUND = new Identifier(Vandalism.getInstance().getId(), "textures/hud/container.png");

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
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public boolean renderPre() {
        return false;
    }

    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
        final MatrixStack matrixStack = context.getMatrices();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(this.color.getRed() / 255F, this.color.getGreen() / 255F, this.color.getBlue() / 255F, this.color.getAlpha() / 255F);
        context.drawTexture(TEXTURE_CONTAINER_BACKGROUND, x, y, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int row = 0;
        for (int i = 0; i < this.items.size(); i++) {
            final ItemStack itemStack = this.items.get(i);
            matrixStack.push();
            matrixStack.translate(0, 0, 401);
            context.drawItem(itemStack, x + 8 + i * 18, y + 7 + row * 18);
            context.drawItemInSlot(textRenderer, itemStack, x + 8 + i * 18, y + 7 + row * 18, null);
            matrixStack.pop();
            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }
    }

}