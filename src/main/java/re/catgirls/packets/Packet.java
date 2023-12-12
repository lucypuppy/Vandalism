package re.catgirls.packets;

import re.catgirls.packets.buffer.PacketBuffer;

@SuppressWarnings("RedundantThrows")
public abstract class Packet {

    public abstract void read(final PacketBuffer buffer) throws Exception;

    public abstract void write(final PacketBuffer buffer) throws Exception;
}