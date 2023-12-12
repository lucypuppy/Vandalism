package re.catgirls.packets.registry;

import re.catgirls.packets.Packet;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class RegisteredPacket {

    private final Class<? extends Packet> packetClass;
    private final Constructor<? extends Packet> constructor;

    public RegisteredPacket(final Class<? extends Packet> packetClass) throws NoSuchMethodException {
        this.packetClass = packetClass;

        final List<Constructor<?>> emptyConstructorList = Arrays.stream(packetClass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0).toList();

        if (emptyConstructorList.isEmpty())
            throw new NoSuchMethodException("Packet %s is missing no-args-constructor".format(packetClass.getName()));

        this.constructor = (Constructor<? extends Packet>) emptyConstructorList.get(0);
    }

    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    public Constructor<? extends Packet> getConstructor() {
        return constructor;
    }
}
