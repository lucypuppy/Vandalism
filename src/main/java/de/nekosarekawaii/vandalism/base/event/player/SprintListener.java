package de.nekosarekawaii.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface SprintListener {

    void onSprint(final SprintEvent event);

    class SprintEvent extends AbstractEvent<SprintListener> {

        public static final int ID = 19;

        public boolean sprinting;
        public boolean force;

        public SprintEvent(final boolean sprinting) {
            this.sprinting = sprinting;
            this.force = false;
        }

        @Override
        public void call(final SprintListener listener) {
            listener.onSprint(this);
        }

    }
}
