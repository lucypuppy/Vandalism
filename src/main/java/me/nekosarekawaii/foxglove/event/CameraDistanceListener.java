package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface CameraDistanceListener {

    void onCameraDistanceGet(final CameraDistanceEvent event);

    class CameraDistanceEvent extends CancellableEvent<CameraDistanceListener> {

        public final static int ID = 11;

        public double desiredCameraDistance;

        public CameraDistanceEvent(double desiredCameraDistance) {
            this.desiredCameraDistance = desiredCameraDistance;
        }

        @Override
        public void call(CameraDistanceListener listener) {
            listener.onCameraDistanceGet(this);
        }
    }
}
