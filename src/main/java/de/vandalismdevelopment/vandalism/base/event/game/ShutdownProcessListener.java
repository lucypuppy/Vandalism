package de.vandalismdevelopment.vandalism.base.event.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface ShutdownProcessListener {

    void onShutdownProcess();

    class ShutdownProcessEvent extends AbstractEvent<ShutdownProcessListener> {

        public static final int ID = 9;

        @Override
        public void call(ShutdownProcessListener listener) {
            listener.onShutdownProcess();
        }
    }

}
