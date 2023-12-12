package de.vandalismdevelopment.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MoveInputListener {

    void onMoveInput(final MoveInputEvent event);

    class MoveInputEvent extends AbstractEvent<MoveInputListener> {

        public static final int ID = 18;

        public float movementForward, movementSideways;
        public final boolean slowDown;
        public final float slowDownFactor;

        public MoveInputEvent(final float movementForward, final float movementSideways, final boolean slowDown, final float slowDownFactor) {
            this.movementForward = movementForward;
            this.movementSideways = movementSideways;
            this.slowDown = slowDown;
            this.slowDownFactor = slowDownFactor;
        }

        @Override
        public void call(final MoveInputListener listener) {
            listener.onMoveInput(this);
        }

    }
}
