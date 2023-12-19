package de.nekosarekawaii.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;

public interface OutgoingPacketListener {

    void onOutgoingPacket(final OutgoingPacketEvent event);

    class OutgoingPacketEvent extends CancellableEvent<OutgoingPacketListener> {

        public static final int ID = 12;

        public Packet<?> packet;
        public final NetworkState networkState;

        public OutgoingPacketEvent(final Packet<?> packet, final NetworkState networkState) {
            this.packet = packet;
            this.networkState = networkState;
        }

        @Override
        public void call(final OutgoingPacketListener listener) {
            listener.onOutgoingPacket(this);
        }

    }

}
