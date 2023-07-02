package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.Listener;
import de.florianmichael.dietrichevents2.type.CancellableEvent;
import net.minecraft.network.packet.Packet;

/**
 * The PacketListener interface represents a listener for network packet events in the Foxglove mod.
 * Classes implementing this interface can listen for packet events and define corresponding event handler methods.
 */
public interface PacketListener extends Listener {

    /**
     * Called when a packet is read from the network.
     *
     * @param event The packet event containing the read packet.
     */
    default void onRead(final PacketEvent event) {
    }

    /**
     * Called when a packet is about to be sent to the network.
     *
     * @param event The packet event containing the packet about to be sent.
     */
    default void onWrite(final PacketEvent event) {
    }

    /**
     * The types of packet events.
     */
    enum PacketEventType {
        READ, WRITE
    }

    /**
     * The PacketEvent class represents a packet event.
     * It encapsulates the type of the event and provides the packet associated with the event.
     */
    class PacketEvent extends CancellableEvent<PacketListener> {

        public final static int ID = 2;

        private final PacketEventType type;

        public Packet<?> packet;

        /**
         * Constructs a new PacketEvent with the specified event type and packet.
         *
         * @param type   The type of the event.
         * @param packet The packet associated with the event.
         */
        public PacketEvent(final PacketEventType type, final Packet<?> packet) {
            this.type = type;
            this.packet = packet;
        }

        /**
         * Calls the appropriate event handler method on the listener based on the event type.
         *
         * @param listener The listener to call the event handler on.
         */
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
