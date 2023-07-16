package me.nekosarekawaii.foxglove.event.impl;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;

public interface WorldListener extends Listener {

    void onWorldLoad();

    class WorldLoadEvent extends AbstractEvent<WorldListener> {

        public final static int ID = 8;

        @Override
        public void call(final WorldListener listener) {
            listener.onWorldLoad();
        }

    }

}
