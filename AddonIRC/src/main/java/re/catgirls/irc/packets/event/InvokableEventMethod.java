package re.catgirls.irc.packets.event;

import re.catgirls.irc.packets.connection.PacketHandler;
import re.catgirls.irc.packets.Packet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class InvokableEventMethod {

    private final Object holder;
    private final Method method;
    private final Class<? extends Packet> packetClass;

    public InvokableEventMethod(final Object holder, final Method method, final Class<? extends Packet> packetClass) {
        this.holder = holder;
        this.method = method;
        this.packetClass = packetClass;

        this.method.setAccessible(true);
    }

    /**
     * Invoke the method
     *
     * @param packet packet
     * @param ctx    packet handler
     * @throws InvocationTargetException if there was an error while invoking the method
     * @throws IllegalAccessException    if the method is inaccessible
     */
    public void invoke(final Packet packet, final PacketHandler ctx) throws InvocationTargetException, IllegalAccessException {
        if (!packetClass.equals(packet.getClass()))
            return;

        Object[] params = new Object[method.getParameterCount()];
        int index = 0;

        for (Parameter parameter : method.getParameters()) {
            if (Packet.class.isAssignableFrom(parameter.getType())) {
                params[index++] = packet;
                continue;
            }

            if (PacketHandler.class.isAssignableFrom(parameter.getType()))
                params[index++] = ctx;
        }

        method.invoke(holder, params);
    }

}
