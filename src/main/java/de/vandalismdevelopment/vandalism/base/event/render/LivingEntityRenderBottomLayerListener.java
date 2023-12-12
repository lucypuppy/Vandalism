package de.vandalismdevelopment.vandalism.base.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityRenderBottomLayerListener {

    default void onLivingEntityRenderBottomLayer(final LivingEntityRenderBottomLayerEvent event) {
    }

    class LivingEntityRenderBottomLayerEvent extends AbstractEvent<LivingEntityRenderBottomLayerListener> {

        public static final int ID = 21;

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
        public void call(final LivingEntityRenderBottomLayerListener listener) {
            listener.onLivingEntityRenderBottomLayer(this);
        }

    }


}
