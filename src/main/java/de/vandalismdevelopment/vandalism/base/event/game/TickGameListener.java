package de.vandalismdevelopment.vandalism.base.event.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface TickGameListener {

    void onTick();

    class TickGameEvent extends AbstractEvent<TickGameListener> {

        public static final int ID = 10;

        @Override
        public void call(final TickGameListener listener) {
            listener.onTick();
        }

    }

}
