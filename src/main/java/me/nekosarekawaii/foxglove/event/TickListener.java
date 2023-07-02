package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;

/**
 * The TickListener interface represents a listener for tick events in the Foxglove mod.
 * Classes implementing this interface can listen for tick events and define corresponding event handler methods.
 */
public interface TickListener extends Listener {

    /**
     * Called on each tick of the game.
     */
    void onTick();

    /**
     * The TickEvent class represents a tick event.
     * It is used to notify the registered listeners that a tick has occurred.
     */
    class TickEvent extends AbstractEvent<TickListener> {

        public final static int ID = 4;

        /**
         * Calls the onTick event handler method on the listener.
         *
         * @param listener The listener to call the event handler on.
         */
        @Override
        public void call(final TickListener listener) {
            listener.onTick();
        }

    }

}
