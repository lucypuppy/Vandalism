package re.catgirls.packets.event;

import re.catgirls.packets.Packet;
import re.catgirls.packets.connection.PacketHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class EventRegistry {

    /**
     * Registered packet listeners
     */
    private final Set<PacketSubscriber> subscribers = new HashSet<>();

    /**
     * Register a packet listener
     *
     * @param holder listener
     */
    public void registerEvents(final Object holder) {
        subscribers.add(new PacketSubscriber(holder));
    }

    /**
     * Invoke a packet listener
     *
     * @param packet packet
     * @param ctx    packet handler
     */
    public void invoke(final Packet packet, final PacketHandler ctx) {
        try {
            for (final PacketSubscriber subscriber : subscribers)
                subscriber.invoke(packet, ctx);
        } catch (InvocationTargetException |
                 IllegalAccessException ignored) {
        }
    }

}
