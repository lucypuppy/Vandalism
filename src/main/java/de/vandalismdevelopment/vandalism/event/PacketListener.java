package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.packet.Packet;

public interface PacketListener {

    void onPacket(final PacketEvent event);

    enum PacketEventState {
        SEND, RECEIVED
    }

    class PacketEvent extends CancellableEvent<PacketListener> {

        public final static int ID = 2;

        public Packet<?> packet;
        public final PacketEventState state;

        public PacketEvent(final Packet<?> packet, final PacketEventState state) {
            this.packet = packet;
            this.state = state;
        }

        @Override
        public void call(final PacketListener listener) {
            listener.onPacket(this);
        }

    }

}
