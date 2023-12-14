package re.catgirls.packets.registry;


import re.catgirls.irc.packet.PacketRegistry;
import re.catgirls.packets.Packet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class SimplePacketRegistry implements IPacketRegistry {

    private final Int2ObjectOpenHashMap<RegisteredPacket> packets = new Int2ObjectOpenHashMap<>();

    @Override
    public void registerPacket(final PacketRegistry.PacketId packetId, final Class<? extends Packet> packet) throws RuntimeException {
        if (containsPacketId(packetId.getId()))
            throw new RuntimeException("PacketID is already in use");

        try {
            final RegisteredPacket registeredPacket = new RegisteredPacket(packet);
            this.packets.put(packetId.getId(), registeredPacket);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("failed to register packet", e);
        }
    }

    @Override
    public int getPacketId(final Class<? extends Packet> packetClass) {
        for (ObjectIterator<Int2ObjectMap.Entry<RegisteredPacket>> it = packets.int2ObjectEntrySet().fastIterator(); it.hasNext(); ) {
            Int2ObjectMap.Entry<RegisteredPacket> entry = it.next();
            if (entry.getValue().getPacketClass().equals(packetClass))
                return entry.getIntKey();
        }

        return -1;
    }

    @Override
    public <T extends Packet> T constructPacket(final int packetId) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return (T) packets.get(packetId).getConstructor().newInstance();
    }

    @Override
    public boolean containsPacketId(final int id) {
        return packets.containsKey(id);
    }

    public Int2ObjectOpenHashMap<RegisteredPacket> getPackets() {
        return packets;
    }
}
