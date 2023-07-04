package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;

public interface TickListener extends Listener {

    void onTick();

    class TickEvent extends AbstractEvent<TickListener> {

        public final static int ID = 4;

        @Override
        public void call(final TickListener listener) {
            listener.onTick();
        }

    }

}
