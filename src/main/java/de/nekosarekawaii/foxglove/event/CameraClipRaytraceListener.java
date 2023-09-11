package de.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface CameraClipRaytraceListener {

    void onCameraClipRaytrace(final CameraClipRaytraceEvent event);

    class CameraClipRaytraceEvent extends CancellableEvent<CameraClipRaytraceListener> {

        public final static int ID = 11;

        public CameraClipRaytraceEvent() {
        }

        @Override
        public void call(final CameraClipRaytraceListener listener) {
            listener.onCameraClipRaytrace(this);
        }

    }

}
