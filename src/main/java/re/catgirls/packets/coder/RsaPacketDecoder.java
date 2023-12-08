package re.catgirls.packets.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import re.catgirls.irc.session.Session;
import re.catgirls.irc.util.EncryptionHelper;
import re.catgirls.packets.Packet;
import re.catgirls.packets.buffer.PacketBuffer;
import re.catgirls.packets.registry.IPacketRegistry;

import java.util.List;

public class RsaPacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final IPacketRegistry packetRegistry;
    private final Session session;

    public RsaPacketDecoder(IPacketRegistry packetRegistry, Session session) {
        this.packetRegistry = packetRegistry;
        this.session = session;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readInt();
        list.add(actuallyDecode(length, session.isEncryptionNotReady() ? byteBuf : EncryptionHelper.decryptByteBuf(session.getClientKeyPair().getPrivate(), byteBuf)));
    }

    private Packet actuallyDecode(int length, ByteBuf byteBuf) throws Exception {
        if (length > 0) {
            int packetId = byteBuf.readInt();

            if (!packetRegistry.containsPacketId(packetId))
                throw new DecoderException("Received invalid packet id");

            PacketBuffer buffer = new PacketBuffer(byteBuf);

            Packet packet = packetRegistry.constructPacket(packetId);
            packet.read(buffer);

            return packet;
        }

        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
