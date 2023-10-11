package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityListener {

    default void onLivingEntityRenderBottomLayer(final LivingEntityRenderBottomLayerEvent event) {}

    class LivingEntityRenderBottomLayerEvent extends AbstractEvent<LivingEntityListener> {

        public final static int ID = 6;

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
        public void call(final LivingEntityListener listener) {
            listener.onLivingEntityRenderBottomLayer(this);
        }

    }

    default void onLivingEntityRenderPost(final LivingEntityRenderPostEvent event) {}

    class LivingEntityRenderPostEvent extends AbstractEvent<LivingEntityListener> {

        public final static int ID = 15;

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
        public void call(final LivingEntityListener listener) {
            listener.onLivingEntityRenderPost(this);
        }

    }

}
