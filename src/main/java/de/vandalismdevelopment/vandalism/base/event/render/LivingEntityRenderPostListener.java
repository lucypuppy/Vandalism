package de.vandalismdevelopment.vandalism.base.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityRenderPostListener {

    void onLivingEntityRenderPost(final LivingEntityRenderPostEvent event);

    class LivingEntityRenderPostEvent extends AbstractEvent<LivingEntityRenderPostListener> {

        public static final int ID = 22;

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
        public void call(final LivingEntityRenderPostListener listener) {
            listener.onLivingEntityRenderPost(this);
        }

    }

}
