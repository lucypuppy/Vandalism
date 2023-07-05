package me.nekosarekawaii.foxglove.event.impl;

import de.florianmichael.dietrichevents2.core.Listener;
import de.florianmichael.dietrichevents2.type.CancellableEvent;
import net.minecraft.network.packet.Packet;

public interface PacketListener extends Listener {


    default void onRead(final PacketEvent event) {
    }

    default void onWrite(final PacketEvent event) {
    }

    enum PacketEventType {
        READ, WRITE
    }

    class PacketEvent extends CancellableEvent<PacketListener> {

        public final static int ID = 2;

        private final PacketEventType type;

        public Packet<?> packet;

        public PacketEvent(final PacketEventType type, final Packet<?> packet) {
            this.type = type;
            this.packet = packet;
        }

        @Override
        public void call(final PacketListener listener) {
            if (this.type == PacketEventType.READ) {
                listener.onRead(this);
            } else {
                listener.onWrite(this);
            }
        }

    }

}
