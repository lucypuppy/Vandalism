package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.packet.Packet;

public interface PacketListener {

    void onPacket(final PacketEvent event);

    class PacketEvent extends CancellableEvent<PacketListener> {

        public final static int ID = 2;

        public Packet<?> packet;
        public final boolean sent;

        public PacketEvent(final Packet<?> packet, final boolean sent) {
            this.packet = packet;
            this.sent = sent;
        }

        @Override
        public void call(final PacketListener listener) {
            listener.onPacket(this);
        }

    }

}
