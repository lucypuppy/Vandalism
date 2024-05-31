package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.CancellableEvent;

public interface FluidPushListener {

    void onFluidPush(final FluidPushEvent fluidPushEvent);

    class FluidPushEvent extends CancellableEvent<FluidPushListener> {

        public final static int ID = 9;

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