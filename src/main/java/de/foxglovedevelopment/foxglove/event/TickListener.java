package de.foxglovedevelopment.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface TickListener {

    void onTick();

    class TickEvent extends AbstractEvent<TickListener> {

        public final static int ID = 4;

        @Override
        public void call(final TickListener listener) {
            listener.onTick();
        }

    }

}
