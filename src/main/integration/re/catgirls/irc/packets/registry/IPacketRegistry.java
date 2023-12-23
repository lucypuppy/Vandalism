package re.catgirls.irc.packets.registry;

import re.catgirls.irc.packet.PacketRegistry;
import re.catgirls.irc.packets.Packet;

import java.lang.reflect.InvocationTargetException;

public interface IPacketRegistry {

    void registerPacket(final PacketRegistry.PacketId packetId, final Class<? extends Packet> packet) throws RuntimeException;

    int getPacketId(Class<? extends Packet> packetClass);

    <T extends Packet> T constructPacket(final int packetId) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    boolean containsPacketId(final int id);

}
