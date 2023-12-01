package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MovementListener {

    default void onSprint(final SprintEvent event) {
    }

    class SprintEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 17;

        public boolean sprinting;

        public SprintEvent(final boolean sprinting) {
            this.sprinting = sprinting;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onSprint(this);
        }

    }

    default void onMoveInput(final MoveInputEvent event) {
    }

    class MoveInputEvent extends AbstractEvent<MovementListener> {

        public final static int ID = 18;

        public float forwardSpeed;
        public float sidewaysSpeed;

        public MoveInputEvent(final float forwardSpeed, final float sidewaysSpeed) {
            this.forwardSpeed = forwardSpeed;
            this.sidewaysSpeed = sidewaysSpeed;
        }

        @Override
        public void call(final MovementListener listener) {
            listener.onMoveInput(this);
        }

    }

}
