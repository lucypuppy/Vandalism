package de.nekosarekawaii.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface RaytraceListener {

    void onRaytrace(final RaytraceEvent event);

    class RaytraceEvent extends AbstractEvent<RaytraceListener> {

        public static final int ID = 33;

        public double range;

        public RaytraceEvent(final double range) {
            this.range = range;
        }

        @Override
        public void call(final RaytraceListener listener) {
            listener.onRaytrace(this);
        }
    }
}
