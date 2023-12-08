package re.catgirls.packets.event;

import re.catgirls.packets.Packet;
import re.catgirls.packets.connection.PacketHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class EventRegistry {

    private final Set<RegisteredPacketSubscriber> subscribers = new HashSet<>();

    public void registerEvents(Object holder) {
        subscribers.add(new RegisteredPacketSubscriber(holder));
    }

    public void invoke(Packet packet, PacketHandler ctx) {
        try {
            for (RegisteredPacketSubscriber subscriber : subscribers) {
                subscriber.invoke(packet, ctx);
            }
        } catch (InvocationTargetException | IllegalAccessException ignored) {
        }
    }

}
