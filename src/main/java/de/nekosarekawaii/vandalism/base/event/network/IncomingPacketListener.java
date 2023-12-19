package de.nekosarekawaii.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public interface IncomingPacketListener {

    void onIncomingPacket(final IncomingPacketEvent event);

    class IncomingPacketEvent extends CancellableEvent<IncomingPacketListener> {

        public static final int ID = 11;

        public Packet<?> packet;
        public final NetworkState networkState;

        public IncomingPacketEvent(final Packet<?> packet, final NetworkState networkState) {
            this.packet = packet;
            this.networkState = networkState;
        }

        @Override
        public void call(final IncomingPacketListener listener) {
            listener.onIncomingPacket(this);
        }

    }

}
