package re.catgirls.irc.packets.event;

import re.catgirls.irc.packets.connection.PacketHandler;
import re.catgirls.irc.packets.Packet;
import re.catgirls.irc.packets.event.interfaces.IPacketSubscriber;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class PacketSubscriber {

    private final Map<Class<? extends Packet>, Set<InvokableEventMethod>> handler = new HashMap<>();

    public PacketSubscriber(final Object subscriberClass) {
        for (Method method : subscriberClass.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(IPacketSubscriber.class))
                continue;

            Class<? extends Packet> packetClass = null;
            for (Parameter parameter : method.getParameters()) {
                if (Packet.class.isAssignableFrom(parameter.getType())) {
                    packetClass = (Class<? extends Packet>) parameter.getType();
                    continue;
                }

                if (PacketHandler.class.isAssignableFrom(parameter.getType()))
                    continue;

                throw new IllegalArgumentException("Invalid parameter for packet subscriber: " + parameter.getType().getSimpleName());
            }

            if (packetClass == null)
                throw new IllegalArgumentException("Missing packet parameter for packet subscriber");

            handler.computeIfAbsent(packetClass, aClass -> new HashSet<>()).add(new InvokableEventMethod(
                    subscriberClass, method, packetClass
            ));
        }
    }

    /**
     * Invoke a packet listener
     *
     * @param rawPacket packet
     * @param ctx       packet handler
     * @throws InvocationTargetException if there was an error while invoking the method
     * @throws IllegalAccessException    if the method is inaccessible
     */
    public void invoke(final Packet rawPacket, final PacketHandler ctx) throws InvocationTargetException, IllegalAccessException {
        final Set<InvokableEventMethod> methods = handler.get(rawPacket.getClass());
        if (methods == null)
            return;

        for (final InvokableEventMethod method : methods)
            method.invoke(rawPacket, ctx);
    }

}
