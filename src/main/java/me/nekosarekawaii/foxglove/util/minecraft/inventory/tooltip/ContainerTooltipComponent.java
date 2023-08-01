package me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ContainerTooltipComponent implements TooltipComponent, ITooltipData {

    private static final Identifier TEXTURE_CONTAINER_BACKGROUND = new Identifier("foxglove", "textures/hud/container.png");

    private final DefaultedList<ItemStack> items;
    private final float[] color;

    public ContainerTooltipComponent(final DefaultedList<ItemStack> items, final float[] color) {
        this.items = items;
        this.color = color;
    }

    @Override
    public int getHeight() {
        return 67;
    }

    @Override
    public int getWidth(final TextRenderer textRenderer) {
        return 176;
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
        context.drawTexture(TEXTURE_CONTAINER_BACKGROUND, x, y, 0, 0, 0, 176, 67, 176, 67);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        int row = 0;
        int i = 0;
        for (var itemStack : items) {
            final MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            matrixStack.translate(0, 0, 401);

            context.drawItem(itemStack, x + 8 + i * 18, y + 7 + row * 18);
            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, itemStack, x + 8 + i * 18, y + 7 + row * 18, null);

            matrixStack.pop();

            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }
    }

}