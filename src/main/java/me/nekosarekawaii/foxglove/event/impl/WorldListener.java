package me.nekosarekawaii.foxglove.event.impl;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface WorldListener {

    void onWorldLoad();

    class WorldLoadEvent extends AbstractEvent<WorldListener> {

        public final static int ID = 8;

        @Override
        public void call(final WorldListener listener) {
            listener.onWorldLoad();
        }

    }

}
