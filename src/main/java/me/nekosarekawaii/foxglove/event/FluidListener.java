package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface FluidListener {

    default void onFluidPush(final FluidPushEvent fluidPushEvent) {
    }

    class FluidPushEvent extends CancellableEvent<FluidListener> {

        public final static int ID = 9;

        public double speed;

        public FluidPushEvent(final double speed) {
            this.speed = speed;
        }

        @Override
        public void call(final FluidListener listener) {
            listener.onFluidPush(this);
        }

    }

}