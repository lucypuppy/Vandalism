package de.vandalismdevelopment.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.util.math.Vec3d;

public interface StrafeListener {

    void onStrafe(final StrafeEvent event);

    class StrafeEvent extends AbstractEvent<StrafeListener> {

        public static final int ID = 20;

        public Vec3d movementInput;
        public float speed, yaw;

        public StrafeEvent(final Vec3d movementInput, final float speed, final float yaw) {
            this.movementInput = movementInput;
            this.speed = speed;
            this.yaw = yaw;
        }

        @Override
        public void call(final StrafeListener listener) {
            listener.onStrafe(this);
        }
    }
}
