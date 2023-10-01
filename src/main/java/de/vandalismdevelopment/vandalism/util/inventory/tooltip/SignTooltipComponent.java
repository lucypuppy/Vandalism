package de.vandalismdevelopment.vandalism.util.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public abstract class SignTooltipComponent<M extends Model> implements ITooltipData, TooltipComponent {

    protected final WoodType type;
    private final SignText front;
    private final SignText back;
    protected final M model;

    public SignTooltipComponent(final WoodType type, final SignText front, final SignText back, final M model) {
        this.type = type;
        this.front = front;
        this.back = back;
        this.model = model;
    }

    public static Optional<TooltipData> fromItemStack(final ItemStack stack) {
        if (stack.getItem() instanceof HangingSignItem signItem) {
            final Block block = signItem.getBlock();
            final NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
            if (nbt != null) return Optional.ofNullable(fromTag(AbstractSignBlock.getWoodType(block), nbt, true));
        } else if (stack.getItem() instanceof SignItem signItem) {
            final Block block = signItem.getBlock();
            final NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
            if (nbt != null) return Optional.ofNullable(fromTag(AbstractSignBlock.getWoodType(block), nbt, false));
        }
        return Optional.empty();
    }

    public static SignTooltipComponent<?> fromTag(final WoodType type, final NbtCompound nbt, final boolean hanging) {
        Optional<SignText> front = Optional.empty();
        Optional<SignText> back = Optional.empty();

        if (nbt.contains("front_text")) {
            front = SignText.CODEC
                    .parse(NbtOps.INSTANCE, nbt.getCompound("front_text"))
                    .resultOrPartial(s -> {
                    })
                    .map(SignTooltipComponent::parseLines);
        }

        if (nbt.contains("back_text")) {
            back = SignText.CODEC
                    .parse(NbtOps.INSTANCE, nbt.getCompound("back_text"))
                    .resultOrPartial(s -> {
                    })
                    .map(SignTooltipComponent::parseLines);
        }

        if (front.isEmpty() && back.isEmpty()) {
            return null;
        } else if (hanging) {
            return new HangingSign(type, front.orElse(null), back.orElse(null));
        } else {
            return new Sign(type, front.orElse(null), back.orElse(null));
        }
    }

    private static SignText parseLines(SignText text) {
        for (int line = 0; line < 4; line++) {
            final Text unfilteredMessage = text.getMessage(line, false), filteredMessage = text.getMessage(line, true);
            text = text.withMessage(line, unfilteredMessage, filteredMessage);
        }

        return text;
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public boolean renderPre() {
        return false;
    }

    protected boolean shouldShowBack() {
        return this.front == null || (this.back != null && Screen.hasControlDown());
    }

    private SignText getText() {
        if (this.shouldShowBack()) return this.back;
        else return this.front;
    }

    private OrderedText[] getOrderedMessages() {
        return this.getText().getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), Text::asOrderedText);
    }

    //TODO recode this rendering and use the SignBlockEntityRenderer for everything.
    @Override
    public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext graphics) {
        DiffuseLighting.enableGuiDepthLighting();
        MatrixStack matrices = graphics.getMatrices();
        matrices.push();
        matrices.translate(x + 2, y, 0);

        matrices.push();
        final VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        final SpriteIdentifier spriteIdentifier = this.getSignTextureId();
        final VertexConsumer vertexConsumer = spriteIdentifier != null ? spriteIdentifier.getVertexConsumer(immediate, this.model::getLayer) : null;
        this.renderModel(graphics, vertexConsumer);
        immediate.draw();
        matrices.pop();

        matrices.translate(0, this.getTextOffset(), 10);

        final OrderedText[] messages = this.getOrderedMessages();
        final int signColor = this.getText().getColor().getSignColor();

        if (this.getText().isGlowing()) {
            for (int i = 0; i < messages.length; i++) {
                final OrderedText text = messages[i];
                textRenderer.drawWithOutline(text, (int) (45 - textRenderer.getWidth(text) / 2.f), i * 10,
                        signColor, SignBlockEntityRenderer.getColor(this.getText()), matrices.peek().getPositionMatrix(), graphics.getVertexConsumers(),
                        LightmapTextureManager.MAX_LIGHT_COORDINATE
                );
            }
        } else {
            for (int i = 0; i < messages.length; i++) {
                final OrderedText text = messages[i];
                graphics.drawText(textRenderer, text, (int) (45 - textRenderer.getWidth(text) / 2.f), i * 10, signColor, false);
            }
        }

        matrices.pop();

        DiffuseLighting.disableGuiDepthLighting();
    }

    public abstract SpriteIdentifier getSignTextureId();

    public abstract void renderModel(DrawContext graphics, VertexConsumer vertexConsumer);

    protected abstract int getTextOffset();

    public static class Sign extends SignTooltipComponent<SignBlockEntityRenderer.SignModel> {

        public Sign(final WoodType type, final SignText front, final SignText back) {
            super(type, front, back, SignBlockEntityRenderer.createSignModel(MinecraftClient.getInstance().getEntityModelLoader(), type));
        }

        @Override
        public int getHeight() {
            return 52;
        }

        @Override
        public int getWidth(final TextRenderer textRenderer) {
            return 94;
        }

        @Override
        public SpriteIdentifier getSignTextureId() {
            return TexturedRenderLayers.getSignTextureId(this.type);
        }

        @Override
        protected int getTextOffset() {
            return 4;
        }

        @Override
        public void renderModel(final DrawContext graphics, final VertexConsumer vertexConsumer) {
            graphics.getMatrices().translate(45, 56, 0);
            graphics.getMatrices().scale(65, 65, -65);
            this.model.stick.visible = false;
            this.model.root.visible = true;
            this.model.root.render(graphics.getMatrices(), vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        }
    }

    public static class HangingSign extends SignTooltipComponent<HangingSignBlockEntityRenderer.HangingSignModel> {
        private final Identifier textureId = new Identifier("textures/gui/hanging_signs/" + this.type.name() + ".png");

        public HangingSign(final WoodType type, final SignText front, final SignText back) {
            super(type, front, back, null);
        }

        @Override
        public int getHeight() {
            return 68;
        }

        @Override
        public int getWidth(final TextRenderer textRenderer) {
            return 94;
        }

        @Override
        public SpriteIdentifier getSignTextureId() {
            return null;
        }

        @Override
        protected int getTextOffset() {
            return 26;
        }

        @Override
        public void renderModel(DrawContext graphics, VertexConsumer vertexConsumer) {
            graphics.getMatrices().translate(44.5, 32, 0);
            RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
            graphics.getMatrices().scale(4.f, 4.f, 1.f);
            graphics.drawTexture(this.textureId, -8, -8, 0.f, 0.f, 16, 16, 16, 16);
        }
    }

}