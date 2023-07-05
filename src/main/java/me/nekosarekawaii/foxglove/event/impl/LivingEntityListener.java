package me.nekosarekawaii.foxglove.event.impl;

import de.florianmichael.dietrichevents2.core.Listener;
import de.florianmichael.dietrichevents2.type.CancellableEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityListener extends Listener {


    void onRenderLivingEntity(final LivingEntityRenderEvent event);


    class LivingEntityRenderEvent extends CancellableEvent<LivingEntityListener> {

        public final static int ID = 6;

        public final LivingEntity livingEntity;

        public float f, g, red, green, blue, alpha;

        public final MatrixStack matrixStack;

        public final VertexConsumerProvider vertexConsumerProvider;

        public int i;

        public boolean showBody, translucent, showOutline;

        public LivingEntityRenderEvent(final LivingEntity livingEntity, final float f, final float g, final MatrixStack matrixStack, final VertexConsumerProvider vertexConsumerProvider, final int i, final boolean showBody, final boolean translucent, final boolean showOutline, final float red, final float green, final float blue, final float alpha) {
            this.livingEntity = livingEntity;
            this.f = f;
            this.g = g;
            this.matrixStack = matrixStack;
            this.vertexConsumerProvider = vertexConsumerProvider;
            this.i = i;
            this.showBody = showBody;
            this.translucent = translucent;
            this.showOutline = showOutline;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Override
        public void call(final LivingEntityListener listener) {
            listener.onRenderLivingEntity(this);
        }

    }

}
