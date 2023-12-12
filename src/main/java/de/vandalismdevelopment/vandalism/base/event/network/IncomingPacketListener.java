package de.vandalismdevelopment.vandalism.base.event.network;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.network.packet.Packet;

public interface IncomingPacketListener {

    void onPacket(final IncomingPacketEvent event);

    class IncomingPacketEvent extends CancellableEvent<IncomingPacketListener> {

        public static final int ID = 11;

        public Packet<?> packet;

        public IncomingPacketEvent(final Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void call(final IncomingPacketListener listener) {
            listener.onPacket(this);
        }

    }

}
