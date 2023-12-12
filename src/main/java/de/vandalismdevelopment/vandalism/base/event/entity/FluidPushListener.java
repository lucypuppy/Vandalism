package de.vandalismdevelopment.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface FluidPushListener {

    void onFluidPush(final FluidPushEvent event);

    class FluidPushEvent extends CancellableEvent<FluidPushListener> {

        public static final int ID = 1;

        public double speed;

        public FluidPushEvent(final double speed) {
            this.speed = speed;
        }

        @Override
        public void call(final FluidPushListener listener) {
            listener.onFluidPush(this);
        }

    }

}
