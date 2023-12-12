package de.vandalismdevelopment.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.packet.Packet;

public interface OutgoingPacketListener {

    void onPacket(final OutgoingPacketEvent event);

    class OutgoingPacketEvent extends CancellableEvent<OutgoingPacketListener> {

        public static final int ID = 12;

        public Packet<?> packet;

        public OutgoingPacketEvent(final Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void call(final OutgoingPacketListener listener) {
            listener.onPacket(this);
        }

    }

}
