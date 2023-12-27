package re.catgirls.irc.packets.coder;

import re.catgirls.irc.session.Session;
import re.catgirls.irc.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import re.catgirls.irc.util.EncryptionHelper;
import re.catgirls.irc.packets.buffer.PacketBuffer;
import re.catgirls.irc.packets.registry.IPacketRegistry;

import java.util.List;

public class RsaPacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final IPacketRegistry packetRegistry;
    private final Session session;

    /**
     * Create a new packet decoder
     *
     * @param packetRegistry packet registry
     * @param session        session
     */
    public RsaPacketDecoder(final IPacketRegistry packetRegistry, final Session session) {
        this.packetRegistry = packetRegistry;
        this.session = session;
    }

    /**
     * Decode a packet from a byte buffer
     *
     * @param channelHandlerContext channel handler context
     * @param byteBuf               byte buffer to decode from
     * @param list                  list to add the decoded packet to
     * @throws Exception if the packet is invalid
     */
    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> list) throws Exception {
        final int length = byteBuf.readInt();
        if (length <= 0) return;

        list.add(readPacket(length, session.isEncryptionNotReady() ?
                byteBuf :
                EncryptionHelper.decryptByteBuf(session.getClientKeyPair().getPrivate(), byteBuf)
        ));
    }

    /**
     * Decode a packet from a byte buffer
     *
     * @param length  length of the packet
     * @param byteBuf byte buffer to decode from
     * @return decoded packet
     * @throws Exception if the packet is invalid
     */
    private Packet readPacket(final int length, final ByteBuf byteBuf) throws Exception {
        final int packetId = byteBuf.readInt();
        if (!packetRegistry.containsPacketId(packetId))
            throw new DecoderException("received invalid packet id");

        /* create new packet buffer & read packet */
        final PacketBuffer buffer = new PacketBuffer(byteBuf);
        final Packet packet = packetRegistry.constructPacket(packetId);
        packet.read(buffer);

        return packet;
    }
}
