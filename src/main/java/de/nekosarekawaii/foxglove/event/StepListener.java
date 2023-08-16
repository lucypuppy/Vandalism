package de.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface StepListener {

    void onStep(final StepEvent event);

    class StepEvent extends AbstractEvent<StepListener> {

        public final static int ID = 10;

        public float stepHeight;

        public StepEvent(final float stepHeight) {
            this.stepHeight = stepHeight;
        }

        @Override
        public void call(final StepListener listener) {
            listener.onStep(this);
        }
    }

}
