package de.nekosarekawaii.vandalism.event.normal.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface SmoothCameraRotationsListener {

    void onSmoothCameraRotations(final SmoothCameraRotationsEvent event);

    class SmoothCameraRotationsEvent extends AbstractEvent<SmoothCameraRotationsListener> {

        public static final int ID = 41;

        public boolean smoothCamera;

        @Override
        public void call(SmoothCameraRotationsListener listener) {
            listener.onSmoothCameraRotations(this);
        }
    }
}
