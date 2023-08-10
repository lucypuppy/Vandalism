package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface CameraListener {

    default void onCameraDistanceGet(final CameraDistanceEvent event) {
    }

    class CameraDistanceEvent extends CancellableEvent<CameraListener> {

        public final static int ID = 11;

        public double desiredCameraDistance;

        public CameraDistanceEvent(final double desiredCameraDistance) {
            this.desiredCameraDistance = desiredCameraDistance;
        }

        @Override
        public void call(final CameraListener listener) {
            listener.onCameraDistanceGet(this);
        }

    }

}
