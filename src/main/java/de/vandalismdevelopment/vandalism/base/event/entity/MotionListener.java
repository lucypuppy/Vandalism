package de.vandalismdevelopment.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.florianmichael.dietrichevents2.StateTypes;

public interface MotionListener {

    default void onPreMotion(final MotionEvent event) {
    }

    default void onPostMotion(final MotionEvent event) {
    }

    class MotionEvent extends AbstractEvent<MotionListener> {

        public static final int ID = 2;

        private final StateTypes state;

        public MotionEvent(final StateTypes state) {
            this.state = state;
        }

        @Override
        public void call(final MotionListener listener) {
            if (this.state == StateTypes.PRE) {
                listener.onPreMotion(this);
            } else {
                listener.onPostMotion(this);
            }
        }
    }

}
