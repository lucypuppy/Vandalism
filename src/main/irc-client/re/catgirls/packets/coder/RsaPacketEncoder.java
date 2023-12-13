package re.catgirls.packets.coder;

import re.catgirls.irc.session.Session;
import re.catgirls.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import re.catgirls.irc.util.EncryptionHelper;
import re.catgirls.packets.buffer.PacketBuffer;
import re.catgirls.packets.registry.IPacketRegistry;

import java.util.List;

public class RsaPacketEncoder extends MessageToMessageEncoder<Packet> {

    private final Session session;
    private final IPacketRegistry packetRegistry;

    /**
     * Create a new packet encoder
     *
     * @param packetRegistry packet registry
     * @param session        session
     */
    public RsaPacketEncoder(final IPacketRegistry packetRegistry, final Session session) {
        this.packetRegistry = packetRegistry;
        this.session = session;
    }

    /**
     * Encode a packet to a byte buffer
     *
     * @param context channel handler context
     * @param packet  packet to encode
     * @param out     list to add the encoded packet to
     * @throws Exception if the packet is invalid
     */
    @Override
    protected void encode(final ChannelHandlerContext context, final Packet packet, final List<Object> out) throws Exception {
        if (session.isEncryptionNotReady()) {
            out.add(writePacket(packet));
            return;
        }

        out.add(EncryptionHelper.encryptByteBuf(session.getServerPublicKey(), writePacket(packet)));
    }

    /**
     * Encode a packet to a byte buffer
     *
     * @param packet packet to encode
     * @return encoded packet
     * @throws Exception if the packet is invalid
     */
    private ByteBuf writePacket(final Packet packet) throws Exception {
        final ByteBuf buf = Unpooled.buffer();

        int packetId = packetRegistry.getPacketId(packet.getClass());
        if (packetId < 0)
            throw new EncoderException("packet id from class %s is <0".formatted(packet.getClass().getName()));

        /* write packet */
        buf.writeInt(packetId);

        PacketBuffer buffer = new PacketBuffer();
        packet.write(buffer);
        buf.writeBytes(buffer);

        return buf;
    }
}