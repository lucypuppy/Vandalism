package de.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface WorldListener {

    void onWorldLoad();

    class WorldLoadEvent extends AbstractEvent<WorldListener> {

        public final static int ID = 7;

        @Override
        public void call(final WorldListener listener) {
            listener.onWorldLoad();
        }

    }

}
