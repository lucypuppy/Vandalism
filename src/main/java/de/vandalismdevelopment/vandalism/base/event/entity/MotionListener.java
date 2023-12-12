package de.vandalismdevelopment.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MotionListener {

    default void onPreMotion(final MotionEvent event) {
    }

    default void onPostMotion(final MotionEvent event) {
    }

    enum MotionEventState {
        PRE, POST
    }

    class MotionEvent extends AbstractEvent<MotionListener> {

        public static final int ID = 2;

        private final MotionEventState state;

        public MotionEvent(final MotionEventState state) {
            this.state = state;
        }

        @Override
        public void call(final MotionListener listener) {
            if (this.state == MotionEventState.PRE) listener.onPreMotion(this);
            else listener.onPostMotion(this);

        }

    }

}
