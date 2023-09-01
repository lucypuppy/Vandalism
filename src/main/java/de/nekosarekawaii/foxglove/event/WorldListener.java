package de.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface WorldListener {

    default void onPreWorldLoad() {
    }

    default void onPostWorldLoad() {
    }

    enum State {
        PRE, POST
    }

    class WorldLoadEvent extends AbstractEvent<WorldListener> {

        public final static int ID = 7;

        private final State state;

        public WorldLoadEvent(final State state) {
            this.state = state;
        }

        @Override
        public void call(final WorldListener listener) {
            if (this.state == State.PRE) listener.onPreWorldLoad();
            else listener.onPostWorldLoad();
        }

    }

}
