package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;

/**
 * The ClientListener interface represents a listener for client-related events in the Foxglove mod.
 * Classes implementing this interface can listen for client events and define corresponding event handler methods.
 */
public interface ClientListener extends Listener {

    /**
     * Called when the client starts.
     * Implement this method to perform any actions when the client starts.
     */
    void onStart();

    /**
     * The types of client events.
     */
    enum ClientEventType {
        START
    }

    /**
     * The ClientEvent class represents a client event.
     * It encapsulates the type of the event and provides a method to call the corresponding event handler on the listener.
     */
    class ClientEvent extends AbstractEvent<ClientListener> {

        public final static int ID = 0;

        private final ClientEventType type;

        /**
         * Constructs a new ClientEvent with the specified event type.
         *
         * @param type The type of the event.
         */
        public ClientEvent(final ClientEventType type) {
            this.type = type;
        }

        /**
         * Calls the appropriate event handler method on the listener based on the event type.
         *
         * @param listener The listener to call the event handler on.
         */
        @Override
        public void call(final ClientListener listener) {
            if (this.type == ClientEventType.START) {
                listener.onStart();
            }
        }

    }

}
