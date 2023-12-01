package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface SprintListener {

    void onSprint(final SprintEvent event);

    class SprintEvent extends AbstractEvent<SprintListener> {

        public final static int ID = 17;

        public boolean sprinting;

        public SprintEvent(final boolean sprinting) {
            this.sprinting = sprinting;
        }

        @Override
        public void call(final SprintListener listener) {
            listener.onSprint(this);
        }

    }

}
