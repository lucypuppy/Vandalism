package re.catgirls.packets.event;

import re.catgirls.packets.Packet;
import re.catgirls.packets.connection.PacketHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class InvokableEventMethod {

    private final Object holder;
    private final Method method;
    private final Class<? extends Packet> packetClass;

    public InvokableEventMethod(Object holder, Method method, Class<? extends Packet> packetClass) {
        this.holder = holder;
        this.method = method;
        this.packetClass = packetClass;

        this.method.setAccessible(true);
    }

    public void invoke(Packet packet, PacketHandler ctx) throws InvocationTargetException, IllegalAccessException {
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
