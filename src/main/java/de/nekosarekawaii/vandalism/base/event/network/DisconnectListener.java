package de.nekosarekawaii.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

public interface DisconnectListener {

    void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason);

    class DisconnectEvent extends AbstractEvent<DisconnectListener> {

        public static final int ID = 30;

        private final ClientConnection clientConnection;
        private final Text disconnectReason;

        public DisconnectEvent(final ClientConnection clientConnection, final Text disconnectReason) {
            this.clientConnection = clientConnection;
            this.disconnectReason = disconnectReason;
        }

        @Override
        public void call(DisconnectListener listener) {
            listener.onDisconnect(this.clientConnection, this.disconnectReason);
        }
    }

}
