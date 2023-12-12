package de.vandalismdevelopment.vandalism.base.event.entity;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface StepListener {

    default void onStep(final StepEvent event) {
    }

    class StepEvent extends AbstractEvent<StepListener> {

        public static final int ID = 3;

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
