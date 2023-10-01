package de.vandalismdevelopment.vandalism.util.inventory.tooltip;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;

public class BannerTooltipComponent implements TooltipComponent, ITooltipData {

    private final ItemStack banner;
    private final ModelPart bannerField = MinecraftClient.getInstance().getEntityModelLoader()
            .getModelPart(EntityModelLayers.BANNER).getChild("flag");

    public BannerTooltipComponent(final ItemStack banner) {
        this.banner = banner;
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

        final VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        this.bannerField.pitch = 0f;
        this.bannerField.pivotY = -32f;

        BannerBlockEntityRenderer.renderCanvas(
                matrices,
                immediate,
                0xF000F0,
                OverlayTexture.DEFAULT_UV,
                this.bannerField,
                ModelLoader.BANNER_BASE,
                true,
                BannerBlockEntity.getPatternsFromNbt(
                        ((BannerItem) this.banner.getItem()).getColor(),
                        BannerBlockEntity.getPatternListNbt(this.banner)
                )
        );

        matrices.pop();
        matrices.pop();
        immediate.draw();
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

}