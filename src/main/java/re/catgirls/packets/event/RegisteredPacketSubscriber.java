package re.catgirls.packets.event;

import re.catgirls.packets.Packet;
import re.catgirls.packets.connection.PacketHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public class RegisteredPacketSubscriber {

    private final Map<Class<? extends Packet>, Set<InvokableEventMethod>> handler = new HashMap<>();

    public RegisteredPacketSubscriber(Object subscriberClass) {
        for (Method method : subscriberClass.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PacketSubscriber.class))
                continue;

            Class<? extends Packet> packetClass = null;
            for (Parameter parameter : method.getParameters()) {
                if (Packet.class.isAssignableFrom(parameter.getType())) {
                    packetClass = (Class<? extends Packet>) parameter.getType();
                    continue;
                }

                if (PacketHandler.class.isAssignableFrom(parameter.getType()))
                    continue;

                throw new IllegalArgumentException("Invalid parameter for @PacketSubscriber: " + parameter.getType().getSimpleName());
            }

            if (packetClass == null)
                throw new IllegalArgumentException("Missing packet parameter for @PacketSubscriber");

            handler.computeIfAbsent(packetClass, aClass -> new HashSet<>()).add(new InvokableEventMethod(
                    subscriberClass, method, packetClass
            ));
        }
    }

    public void invoke(Packet rawPacket, PacketHandler ctx) throws InvocationTargetException, IllegalAccessException {
        Set<InvokableEventMethod> methods = handler.get(rawPacket.getClass());
        if (methods == null)
            return;

        for (InvokableEventMethod method : methods) {
            method.invoke(rawPacket, ctx);
        }
    }

}
