package re.catgirls.packets.registry;

import re.catgirls.irc.packet.PacketRegistry;
import re.catgirls.packets.Packet;

import java.lang.reflect.InvocationTargetException;

public interface IPacketRegistry {

    void registerPacket(PacketRegistry.PacketId packetId, Packet packet) throws RuntimeException;

    void registerPacket(PacketRegistry.PacketId packetId, Class<? extends Packet> packet) throws RuntimeException;

    int getPacketId(Class<? extends Packet> packetClass);

    <T extends Packet> T constructPacket(int packetId) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    boolean containsPacketId(int id);

}
