package de.nekosarekawaii.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.util.math.Vec3d;

public interface StepSuccessListener {

    default void onStepSuccess(final StepSuccessEvent event) {
    }

    class StepSuccessEvent extends AbstractEvent<StepSuccessListener> {

        public static final int ID = 4;

        public final Vec3d movement;
        public Vec3d adjustMovementForCollisions;

        public StepSuccessEvent(final Vec3d movement, final Vec3d adjustMovementForCollisions) {
            this.movement = movement;
            this.adjustMovementForCollisions = adjustMovementForCollisions;
        }

        @Override
        public void call(final StepSuccessListener listener) {
            listener.onStepSuccess(this);
        }

    }

}
