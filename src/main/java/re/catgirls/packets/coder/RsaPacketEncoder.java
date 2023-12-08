package re.catgirls.packets.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import re.catgirls.irc.session.Session;
import re.catgirls.irc.util.EncryptionHelper;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;
import re.catgirls.packets.registry.IPacketRegistry;

import java.util.List;

public class RsaPacketEncoder extends MessageToMessageEncoder<Packet> {

    private final Session session;
    private final IPacketRegistry packetRegistry;

    public RsaPacketEncoder(IPacketRegistry packetRegistry, Session session) {
        this.packetRegistry = packetRegistry;
        this.session = session;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Packet packet, List<Object> out) throws Exception {
        if (session.isEncryptionNotReady()) {
            out.add(actuallyEncode(packet));
            return;
        }

        out.add(EncryptionHelper.encryptByteBuf(session.getServerPublicKey(), actuallyEncode(packet)));
    }

    private ByteBuf actuallyEncode(Packet packet) throws Exception {
        ByteBuf buf = Unpooled.buffer();

        int packetId = packetRegistry.getPacketId(packet.getClass());
        if (packetId < 0) {
            throw new EncoderException("Returned PacketId by registry is < 0");
        }

        buf.writeInt(packetId);

        PacketBuffer buffer = new PacketBuffer();
        packet.write(buffer);

        buf.writeBytes(buffer);
        return buf;
    }
}