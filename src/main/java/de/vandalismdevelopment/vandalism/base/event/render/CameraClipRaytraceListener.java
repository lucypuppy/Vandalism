package de.vandalismdevelopment.vandalism.base.event.render;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface CameraClipRaytraceListener {

    void onCameraClipRaytrace(final CameraClipRaytraceEvent event);

    class CameraClipRaytraceEvent extends CancellableEvent<CameraClipRaytraceListener> {

        public static final int ID = 20;

        @Override
        public void call(final CameraClipRaytraceListener listener) {
            listener.onCameraClipRaytrace(this);
        }
    }

}
