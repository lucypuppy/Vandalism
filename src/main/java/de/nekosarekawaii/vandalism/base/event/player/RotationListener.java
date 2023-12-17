package de.nekosarekawaii.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface RotationListener {

    void onRotation(final RotationEvent event);

    class RotationEvent extends AbstractEvent<RotationListener> {

        public static final int ID = 27;

        @Override
        public void call(final RotationListener listener) {
            listener.onRotation(this);
        }

    }

}