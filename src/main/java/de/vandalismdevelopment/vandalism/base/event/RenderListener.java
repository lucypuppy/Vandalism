package de.vandalismdevelopment.vandalism.base.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface RenderListener {

    default void onRender2DInGame(final DrawContext context, final float delta) {
    }

    default void onRender2DOutGamePre(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    default void onRender2DOutGamePost(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    enum Render2DEventType {
        IN_GAME, OUT_GAME_PRE, OUT_GAME_POST
    }

    class Render2DEvent extends AbstractEvent<RenderListener> {

        public static final int ID = 3;

        private final Render2DEventType type;
        private final DrawContext context;
        private final int mouseX, mouseY;
        private final float delta;

        public Render2DEvent(final DrawContext context, final int mouseX, final int mouseY, final float delta, final boolean post) {
            this.type = post ? Render2DEventType.OUT_GAME_POST : Render2DEventType.OUT_GAME_PRE;
            this.context = context;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.delta = delta;
        }

        public Render2DEvent(final DrawContext context, final float delta) {
            this.type = Render2DEventType.IN_GAME;
            this.context = context;
            this.delta = delta;
            this.mouseX = 0;
            this.mouseY = 0;
        }

        @Override
        public void call(final RenderListener listener) {
            switch (this.type) {
                case IN_GAME -> listener.onRender2DInGame(this.context, this.delta);
                case OUT_GAME_PRE -> listener.onRender2DOutGamePre(this.context, this.mouseX, this.mouseY, this.delta);
                case OUT_GAME_POST -> listener.onRender2DOutGamePost(this.context, this.mouseX, this.mouseY, this.delta);
                default -> {}
            }
        }

    }

    default void onTextDraw(final TextDrawEvent event) {
    }

    class TextDrawEvent extends AbstractEvent<RenderListener> {

        public static final int ID = 16;

        public String text;

        public TextDrawEvent(final String text) {
            this.text = text;
        }

        @Override
        public void call(final RenderListener listener) {
            listener.onTextDraw(this);
        }

    }

    default void onLivingEntityRenderBottomLayer(final LivingEntityRenderBottomLayerEvent event) {
    }

    class LivingEntityRenderBottomLayerEvent extends AbstractEvent<RenderListener> {

        public static final int ID = 6;

        public final LivingEntity livingEntity;

        public final MatrixStack matrixStack;

        public final VertexConsumer vertexConsumer;

        public int light, overlay;

        public float red, green, blue, alpha;

        public LivingEntityRenderBottomLayerEvent(final LivingEntity livingEntity, final MatrixStack matrixStack, final VertexConsumer vertexConsumer, final int light, final int overlay, final float red, final float green, final float blue, final float alpha) {
            this.livingEntity = livingEntity;
            this.matrixStack = matrixStack;
            this.vertexConsumer = vertexConsumer;
            this.light = light;
            this.overlay = overlay;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public void call(final RenderListener listener) {
            listener.onLivingEntityRenderBottomLayer(this);
        }

    }

    default void onLivingEntityRenderPost(final LivingEntityRenderPostEvent event) {
    }

    class LivingEntityRenderPostEvent extends AbstractEvent<RenderListener> {

        public static final int ID = 15;

        public final LivingEntity livingEntity;

        public float yaw, tickDelta;

        public final MatrixStack matrixStack;

        public int light;

        public LivingEntityRenderPostEvent(final LivingEntity livingEntity, final float yaw, final float tickDelta, final MatrixStack matrixStack, final int light) {
            this.livingEntity = livingEntity;
            this.yaw = yaw;
            this.tickDelta = tickDelta;
            this.matrixStack = matrixStack;
            this.light = light;
        }

        @Override
        public void call(final RenderListener listener) {
            listener.onLivingEntityRenderPost(this);
        }

    }

    default void onTooltipDraw(final TooltipDrawEvent event) {
    }

    class TooltipDrawEvent extends AbstractEvent<RenderListener> {

        public static final int ID = 12;

        public ItemStack itemStack;

        public final List<TooltipData> tooltipData;

        public TooltipDrawEvent(final ItemStack itemStack, final List<TooltipData> tooltipData) {
            this.itemStack = itemStack;
            this.tooltipData = tooltipData;
        }

        @Override
        public void call(final RenderListener listener) {
            listener.onTooltipDraw(this);
        }

    }

    default void onCameraClipRaytrace(final CameraClipRaytraceEvent event) {
    }

    class CameraClipRaytraceEvent extends CancellableEvent<RenderListener> {

        public static final int ID = 11;

        public CameraClipRaytraceEvent() {
        }

        @Override
        public void call(final RenderListener listener) {
            listener.onCameraClipRaytrace(this);
        }

    }

}
